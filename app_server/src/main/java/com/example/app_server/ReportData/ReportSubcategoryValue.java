package com.example.app_server.ReportData;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ReportSubcategoryValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;


    @ManyToOne(fetch = FetchType.LAZY) // or LAZY if you're manually loading
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @OneToMany(mappedBy = "subcategoryValue", cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReportSubcategoryFieldValue> fieldValues = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Subcategory getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(Subcategory subcategory) {
        this.subcategory = subcategory;
    }

    public List<ReportSubcategoryFieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<ReportSubcategoryFieldValue> fieldValues) {
        this.fieldValues = fieldValues;
    }
}
