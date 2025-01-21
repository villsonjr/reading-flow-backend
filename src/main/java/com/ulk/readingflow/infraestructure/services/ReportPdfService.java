package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.v1.payloads.responses.dtos.ReadingDTO;
import com.ulk.readingflow.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportPdfService {

    @Autowired
    private ReadingService readingService;
    private final TemplateEngine templateEngine;

    @Value("${report.page.size}")
    private Integer pageSize;

    public ReportPdfService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    public byte[] generatePdf(String templateName, User user) {
        List<ReadingDTO> readBooksDTO = this.readingService.listAllReadBooks(user);
        List<List<ReadingDTO>> paginatedReadings = new ArrayList<>();

        for (int i = 0; i < readBooksDTO.size(); i += this.pageSize) {
            int end = Math.min(i + this.pageSize, readBooksDTO.size());
            paginatedReadings.add(readBooksDTO.subList(i, end));
        }

        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("paginatedReadings", paginatedReadings);
        context.setVariable("totalPages", paginatedReadings.size());

        String htmlContent = templateEngine.process(templateName, context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}
