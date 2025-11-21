package com.example.tracker.controller;

import com.example.tracker.model.Expense;
import com.example.tracker.service.BudgetService;
import com.example.tracker.service.ExpenseService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import jakarta.servlet.http.HttpServletResponse;

// PDF Libraries
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;

    public ExpenseController(ExpenseService expenseService, BudgetService budgetService) {
        this.expenseService = expenseService;
        this.budgetService = budgetService;
    }

    // ---------------------- PDF EXPORT ----------------------
    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");

        List<Expense> expenses = expenseService.getAll();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Title
        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph("Expense Tracker Report", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Summary Calculations
        double income = expenses.stream()
                .filter(e -> "INCOME".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        double expense = expenses.stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();

        double balance = income - expense;

        Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);

        document.add(new Paragraph("Total Income: " + income));
        document.add(new Paragraph("Total Expense: " + expense));
        document.add(new Paragraph("Balance: " + balance));
        document.add(new Paragraph("\n"));

        // Create Table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        String[] headers = {"Title", "Amount", "Category", "Type", "Date"};

        // Table Header Row
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, bold));
            cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
            table.addCell(cell);
        }

        // Table Data Rows
        for (Expense e : expenses) {
            table.addCell(e.getTitle());
            table.addCell(String.valueOf(e.getAmount()));
            table.addCell(e.getCategory());
            table.addCell(e.getType());
            table.addCell(e.getDate() != null ? e.getDate().toString() : "");
        }

        document.add(table);
        document.close();
    }

    // ---------------------- HOME PAGE ----------------------
    @GetMapping("/")
    public String home(Model model) {

        List<Expense> list = expenseService.getAll();

        double income = expenseService.totalIncome();
        double expense = expenseService.totalExpense();
        double balance = income - expense;

        double budget = budgetService.getBudget();
        double remaining = budget - expense;

        model.addAttribute("list", list);
        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("balance", balance);
        model.addAttribute("budget", budget);
        model.addAttribute("remaining", remaining);

        return "index";
    }

    // ---------------------- ADD EXPENSE ----------------------
    @PostMapping("/add")
    public String add(Expense e) {
        expenseService.save(e);
        return "redirect:/";
    }

    // ---------------------- DELETE EXPENSE ----------------------
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        expenseService.delete(id);
        return "redirect:/";
    }

    // ---------------------- UPDATE BUDGET ----------------------
    @PostMapping("/budget")
    public String updateBudget(@RequestParam double amount) {
        budgetService.updateBudget(amount);
        return "redirect:/";
    }

    // ---------------------- CHART DATA API ----------------------
    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Double> chartData() {

        Map<String, Double> data = new HashMap<>();

        for (Expense e : expenseService.getAll()) {
            if (!"EXPENSE".equalsIgnoreCase(e.getType())) continue;

            data.put(e.getCategory(),
                    data.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        return data;
    }
}
