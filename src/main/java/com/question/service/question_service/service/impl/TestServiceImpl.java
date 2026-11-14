package com.question.service.question_service.service.impl;

import com.question.service.question_service.dto.request.CreateTestRequest;
import com.question.service.question_service.dto.response.*;
import com.question.service.question_service.exception.BadRequestException;
import com.question.service.question_service.exception.DuplicateResourceException;
import com.question.service.question_service.exception.ResourceNotFoundException;
import com.question.service.question_service.exception.UnauthorizedException;
import com.question.service.question_service.models.*;
import com.question.service.question_service.repository.*;
import com.question.service.question_service.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_INVITES_PER_REQUEST = 50;

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final TestInviteDispatcher testInviteDispatcher;
    private final TestCaseRepository testCaseRepository;
    private final CompanyRepository companyRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final MultiThreadImpl multiThreadImpl;


    @Override
    @Transactional
    public TestResponse createTest(CreateTestRequest request) {
        if (testRepository.existsByTestName(request.getTestName())) {
            throw new DuplicateResourceException("Test with name '" + request.getTestName() + "' already exists");
        }
        User user = getCurrentUser();
        Boolean isAdmin = user.getRole() == User.Role.ADMIN;
        Boolean isCaAdmin = user.getRole() == User.Role.CAADMIN;
        Company company = user.getCompany();
        if(isAdmin){
            if(request.getCompanyId() == null){
                throw new BadRequestException("Company Id is required");
            }
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));
        }
        Test test = Test.builder()
                .testName(request.getTestName())
                .totalTimeSeconds((long) request.getTotalTimeMinute() * 60)
                .createdBy(user.getUserId())
                .isCreatedByCAAdmin(isCaAdmin)
                .isCreatedByAdmin(isAdmin)
                .company(company)
                .build();

        Test saved = testRepository.save(test);
        log.info("Created test with id={} name='{}' by createdBy={}", saved.getTestId(), saved.getTestName(), saved.getCreatedBy());

        return TestResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestResponse> getAllTest(int page, int size, String sortBy, String status, String sortDir, String query, UUID companyId) {
        User user = getCurrentUser();
        if(user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.CAADMIN) {
            throw new UnauthorizedException("You are not authorized to perform this action");
        }
        if(companyId==null && user.getRole() == User.Role.ADMIN) {
            throw new UnauthorizedException("You are ADMIN, you must provide companyId to get all tests");
        }
        Company company = companyId != null ? companyRepository.findById(companyId).orElse(null) : null;
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Test> testPage;
        if (query != null && !query.isEmpty()) {
            if(user.getRole() == User.Role.ADMIN) {
                testPage = testRepository.findByTestNameContainingIgnoreCase(query, pageable);
            } else if(user.getRole() == User.Role.CAADMIN) {
                testPage = testRepository.findByTestNameContainingIgnoreCaseAndCompany_CompanyId(query, user.getCompany().getCompanyId(), pageable);
            } else {
                throw new UnauthorizedException("You are not authorized to perform this action");
            }
        } else {
            if(user.getRole() == User.Role.ADMIN) {
                testPage = testRepository.findAll(pageable);
            } else if(user.getRole() == User.Role.CAADMIN) {
                testPage = testRepository.findByCompany_CompanyId(user.getCompany().getCompanyId(), pageable);
            } else {
                throw new UnauthorizedException("You are not authorized to perform this action");
            }
        }
        if(company!=null) {
            List<Test> filtered = testPage.getContent().stream()
                    .filter(test -> test.getCompany() != null
                            && test.getCompany().getCompanyId().equals(companyId))
                    .collect(Collectors.toList());
            testPage = new PageImpl<>(filtered, testPage.getPageable(), filtered.size());
        }
        Page<TestResponse> responsePage = testPage.map(test ->
                TestResponse.builder()
                        .testId(test.getTestId())
                        .testName(test.getTestName())
                        .totalTimeMinute((int) (test.getTotalTimeSeconds() / 60))
                        .createdBy(test.getCreatedBy())
                        .createdTime(test.getCreatedTime())
                        .status(test.getStatus().name())
                        .totalQuestions(test.getCodingQuestions().size() + test.getTestQuestions().size())
                        .totalCodingQuestions(test.getCodingQuestions().size())
                        .build()
        );
        return responsePage;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTestResponse getAllQuestionFromTest(String testId) {
        Test test = testRepository.findById(UUID.fromString(testId))
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));

        List<McqResponse> mcqResponses = new ArrayList<>();
        if (test.getTestQuestions() != null) {
            for (TestQuestion tq : test.getTestQuestions()) {
                String question = tq.getQuestion();
                mcqResponses.add(McqResponse.builder()
                        .questionId(tq.getQuestionId())
                        .testId(tq.getTest().getTestId())
                        .question(question)
                        .questionImageUrl(tq.getQuestionImageUrl())
                        .optionA(tq.getOptionA())
                        .optionB(tq.getOptionB())
                        .optionC(tq.getOptionC())
                        .optionD(tq.getOptionD())
                        .correctOption(tq.getCorrectOption().name())
                        .marks(tq.getMarks())
                        .orderIndex(tq.getOrderIndex())
                        .level(tq.getLevel().name())
                        .build());
            }
        }

        List<CodingQuestionResponse> codingQuestionResponses = new ArrayList<>();
        if (test.getCodingQuestions() != null) {
            for (CodingQuestion cq : test.getCodingQuestions()) {
                List<TestCase> testCases=testCaseRepository.findByCodingQuestion_CodingQuestionId(cq.getCodingQuestionId());
                List<TestCaseResponse> testCaseResponses = new ArrayList<>();
                for (TestCase tc : testCases) {
                    testCaseResponses.add(TestCaseResponse.from(tc));
                }
                codingQuestionResponses.add(CodingQuestionResponse.builder()
                        .codingQuestionId(cq.getCodingQuestionId())
                        .testId(cq.getTest().getTestId())
                        .title(cq.getTitle())
                        .description(cq.getDescription())
                        .paragraphs(new ArrayList<>(cq.getParagraphs()))
                        .constraints(new ArrayList<>(cq.getConstraints()))
                        .imageUrl1(cq.getImageUrl1())
                        .imageUrl2(cq.getImageUrl2())
                        .difficulty(cq.getDifficulty().name())
                        .marks(cq.getMarks())
                        .orderIndex(cq.getOrderIndex())
                                .testCaseResponse(testCaseResponses)
                        .build());
            }
        }

        return AdminTestResponse.builder()
                .testId(test.getTestId())
                .testName(test.getTestName())
                .totalTimeMinute((int) (test.getTotalTimeSeconds() / 60))
                .createdBy(test.getCreatedBy())
                .createdTime(test.getCreatedTime())
                .status(test.getStatus().name())
                .totalQuestions(mcqResponses.size() + codingQuestionResponses.size())
                .totalCodingQuestions(codingQuestionResponses.size())
                .mcqQuestions(mcqResponses)
                .codingQuestionResponses(codingQuestionResponses)
                .build();
    }


    @Override
    public void changeTestStatus(UUID testId, String targetStatus) {
        if (!testRepository.existsById(testId)) {
            throw new ResourceNotFoundException("Test not found with id: " + testId);
        }
        if (targetStatus == null || targetStatus.isEmpty()) {
            throw new BadRequestException("targetStatus must not be empty");
        }
        Test test = testRepository.findById(testId).get();
        if (test.getStatus().name().equals(targetStatus)) {
            throw new BadRequestException("Already in target status");
        }
        if (targetStatus.equals("DRAFT")) {
            test.setStatus(Test.Status.DRAFT);
        } else if (targetStatus.equals("ACTIVE")) {
            test.setStatus(Test.Status.ACTIVE);
        } else if (targetStatus.equals("ARCHIVED")) {
            test.setStatus(Test.Status.ARCHIVED);
        } else {
            throw new BadRequestException("Target Status is incorrect");
        }
        testRepository.save(test);
    }

    @Override
    @Transactional(readOnly = true)
    public String sendTestInvite(UUID testId, String emails) {
        User user = getCurrentUser();
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));

        if (test.getStatus() != Test.Status.ACTIVE) {
            throw new BadRequestException("Test Status should be active");
        }
        if (user.getRole() != User.Role.ADMIN && !test.getCreatedBy().equals(user.getUserId())) {
            throw new UnauthorizedException("You are not authorized to send invites for this test");
        }

        Set<String> emailSet = parseAndValidateEmails(emails);

        // Force-initialize the lazy collections the invite email template reads, since
        // dispatch() continues on a different thread after this transaction/session closes.
        test.getCodingQuestions().size();
        test.getTestQuestions().size();
        System.out.println("Calling send email job");
        testInviteDispatcher.dispatch(user, test, emailSet);

        return "Sending email done";
    }

    @Override
    @Transactional(readOnly = true)
    public BulkTemplateResponse getBulkTemplate(UUID testId) {
        Test test= testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));
        User user = getCurrentUser();
        if (user.getRole() == User.Role.CANDIDATE) {
            throw new UnauthorizedException("You are not authorized to get bulk template for this test");
        }
        byte[] content = exportTest(test);
        String base64Content = Base64.getEncoder().encodeToString(content);
        BulkTemplateResponse response= BulkTemplateResponse.builder()
                .testId(test.getTestId())
                .name(test.getTestName())
                .type("xlsx")
                .content(base64Content)
                .build();
        return response;
    }

    @Override
    public BulkUploadResponse bulkUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file must not be empty");
        }

        UUID testId;
        List<BulkMcqRow> rows;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            testId = readTestId(workbook);
            rows = readQuestionRows(workbook);
        } catch (IOException e) {
            throw new BadRequestException("Unable to read uploaded file: " + e.getMessage());
        }

        if (!testRepository.existsById(testId)) {
            throw new ResourceNotFoundException("Test not found with id: " + testId);
        }
        if (rows.isEmpty()) {
            throw new BadRequestException("Uploaded file has no questions to process");
        }

        int baseOrderIndex = testQuestionRepository.countByTest_TestId(testId);
        int newRowSeq = 0;
        List<CompletableFuture<BulkUploadRowResult>> futures = new ArrayList<>(rows.size());
        for (BulkMcqRow row : rows) {
            if (row.questionId() == null) {
                row = row.withOrderIndex(baseOrderIndex + newRowSeq++);
            }
            futures.add(multiThreadImpl.processRow(row, testId));
        }

        List<BulkUploadRowResult> results = futures.stream().map(CompletableFuture::join).toList();
        List<String> errors = results.stream()
                .filter(r -> !r.success())
                .map(r -> "Row " + r.rowNumber() + ": " + r.error())
                .toList();

        log.info("Bulk upload for test {} finished: {} succeeded, {} failed",
                testId, results.size() - errors.size(), errors.size());

        return BulkUploadResponse.builder()
                .testId(testId)
                .totalRows(results.size())
                .succeeded(results.size() - errors.size())
                .failed(errors.size())
                .errors(errors)
                .build();
    }

    private UUID readTestId(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Test Details");
        if (sheet == null) {
            throw new BadRequestException("Uploaded file is missing the 'Test Details' sheet");
        }
        for (Row row : sheet) {
            String label = getCellString(row.getCell(0));
            if ("Test Id".equals(label)) {
                String value = getCellString(row.getCell(1));
                try {
                    return UUID.fromString(value);
                } catch (Exception e) {
                    throw new BadRequestException("Invalid Test Id in uploaded file: " + value);
                }
            }
        }
        throw new BadRequestException("Could not find Test Id in the uploaded file's 'Test Details' sheet");
    }

    private List<BulkMcqRow> readQuestionRows(Workbook workbook) {
        Sheet sheet = workbook.getSheet("questions");
        if (sheet == null) {
            throw new BadRequestException("Uploaded file is missing the 'questions' sheet");
        }

        List<BulkMcqRow> rows = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            String question = getCellString(row.getCell(1));
            if (question == null || question.isBlank()) {
                continue;
            }

            String questionIdStr = getCellString(row.getCell(0));
            UUID questionId = null;
            if (questionIdStr != null && !questionIdStr.isBlank()) {
                try {
                    questionId = UUID.fromString(questionIdStr.trim());
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid questionId '" + questionIdStr + "' at row " + (r + 1));
                }
            }

            rows.add(new BulkMcqRow(
                    r + 1,
                    questionId,
                    question,
                    getCellString(row.getCell(2)),
                    getCellString(row.getCell(3)),
                    getCellString(row.getCell(4)),
                    getCellString(row.getCell(5)),
                    getCellString(row.getCell(6)),
                    getCellString(row.getCell(7)),
                    (int) getCellNumeric(row.getCell(8), 1),
                    0
            ));
        }
        return rows;
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double num = cell.getNumericCellValue();
                yield num == Math.floor(num) ? String.valueOf((long) num) : String.valueOf(num);
            }
            case BLANK -> null;
            case FORMULA -> cell.getStringCellValue();
            default -> cell.toString();
        };
    }

    private double getCellNumeric(Cell cell, double defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private Set<String> parseAndValidateEmails(String emails) {
        if (emails == null || emails.isBlank()) {
            throw new BadRequestException("emails must not be empty");
        }

        Set<String> emailSet = Arrays.stream(emails.split(","))
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (emailSet.isEmpty()) {
            throw new BadRequestException("emails must not be empty");
        }
        if (emailSet.size() > MAX_INVITES_PER_REQUEST) {
            throw new BadRequestException("Cannot invite more than " + MAX_INVITES_PER_REQUEST + " emails in a single request");
        }

        List<String> invalid = emailSet.stream()
                .filter(email -> !EMAIL_PATTERN.matcher(email).matches())
                .toList();
        if (!invalid.isEmpty()) {
            throw new BadRequestException("Invalid email address(es): " + String.join(", ", invalid));
        }

        return emailSet;
    }

    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName == null || userName.isEmpty()) {
            throw new BadRequestException("You are not authorized to perform this action");
        }
        User user = userRepository.findByUserId(UUID.fromString(userName))
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userName));
        return user;
    }

    public byte[] exportTest(Test test) {
        List<TestQuestion> questions = test.getTestQuestions();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            buildTestDetailsSheet(workbook, test);
            buildQuestionsSheet(workbook, questions);
            workbook.write(out);        // write into the stream
        } catch (IOException e) {       // workbook auto-closed HERE, after write
            throw new RuntimeException("Failed to generate Excel", e);
        }
        return out.toByteArray();
    }

    private void buildTestDetailsSheet(Workbook workbook, Test test) {
        Sheet sheet = workbook.createSheet("Test Details");

        // --- Style for the merged title: yellow bg, centered, bold ---
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);

        // --- Merge A1:G3 (rows 0-2, cols 0-6) and write the title ---
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 6));
        // Every cell in the merged region needs the style applied for the fill to cover it fully
        for (int r = 0; r <= 2; r++) {
            Row row = sheet.createRow(r);
            for (int c = 0; c <= 6; c++) {
                row.createCell(c).setCellStyle(titleStyle);
            }
        }
        sheet.getRow(0).getCell(0).setCellValue(test.getTestName());

        // --- Label style (bold) for column A labels ---
        CellStyle labelStyle = workbook.createCellStyle();
        Font labelFont = workbook.createFont();
        labelFont.setBold(true);
        labelStyle.setFont(labelFont);

        // --- Detail rows (row index 3 = spreadsheet row 4) ---
        createLabelValue(sheet, 3, "Test Id", test.getTestId().toString(), labelStyle);
        createLabelValue(sheet, 4, "Created By", test.getCreatedBy().toString(), labelStyle);
        createLabelValue(sheet, 5, "Total Time(min)", String.valueOf(test.getTotalTimeSeconds()/60), labelStyle);
        createLabelValue(sheet, 6, "Test Status", test.getStatus().toString(), labelStyle);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createLabelValue(Sheet sheet, int rowIdx, String label, String value, CellStyle labelStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        row.createCell(1).setCellValue(value != null ? value : "");
    }

    private void buildQuestionsSheet(Workbook workbook, List<TestQuestion> questions) {
        Sheet sheet = workbook.createSheet("questions");

        // --- Header style: bold ---
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        String[] headers = {
                "questionId", "question", "option_a", "option_b", "option_c",
                "option_d", "correct_option", "level", "marks"
        };
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (TestQuestion q : questions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(q.getQuestionId().toString());
            row.createCell(1).setCellValue(q.getQuestion());
            row.createCell(2).setCellValue(q.getOptionA());
            row.createCell(3).setCellValue(q.getOptionB());
            row.createCell(4).setCellValue(q.getOptionC());
            row.createCell(5).setCellValue(q.getOptionD());
            row.createCell(6).setCellValue(q.getCorrectOption().toString());
            row.createCell(7).setCellValue(q.getLevel().toString());
            row.createCell(8).setCellValue(q.getMarks());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}
