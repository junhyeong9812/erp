package com.erp.production.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "production_bom")
public class BillOfMaterials extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long finishedProductId;

    @ElementCollection
    @CollectionTable(name = "production_bom_line", joinColumns = @JoinColumn(name = "bom_id"))
    private List<Component> components = new ArrayList<>();

    protected BillOfMaterials() {}

    public static BillOfMaterials of(Long finishedProductId, List<Component> components) {
        BillOfMaterials b = new BillOfMaterials();
        b.finishedProductId = finishedProductId;
        b.components = new ArrayList<>(components);
        return b;
    }

    public List<Component> requirementsFor(int finishedQty) {
        List<Component> req = new ArrayList<>();
        for (Component c : components) {
            req.add(new Component(c.componentProductId(), c.quantityPerUnit() * finishedQty));
        }
        return req;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getFinishedProductId() { return finishedProductId; }
    public List<Component> getComponents() { return Collections.unmodifiableList(components); }

    @Embeddable
    public record Component(Long componentProductId, int quantityPerUnit) {}
}