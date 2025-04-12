package com.example.app_server.ReportData;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ReportSubcategoryFieldValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;
    private String fieldValue;

    @ManyToOne
    @JoinColumn(name = "subcategory_value_id")
    private ReportSubcategoryValue subcategoryValue;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public ReportSubcategoryValue getSubcategoryValue() {
        return subcategoryValue;
    }

    public void setSubcategoryValue(ReportSubcategoryValue subcategoryValue) {
        this.subcategoryValue = subcategoryValue;
    }
}
