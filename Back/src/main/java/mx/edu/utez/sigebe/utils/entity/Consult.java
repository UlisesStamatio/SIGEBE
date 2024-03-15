package mx.edu.utez.sigebe.utils.entity;

import java.util.List;

public class Consult {
    private List entities;
    private long total;

    private long extraData;

    public Consult() {
    }

    public Consult(List entities, long total) {
        this.entities = entities;
        this.total = total;
    }

    public Consult(List entities, long total, long extraData) {
        this.entities = entities;
        this.total = total;
        this.extraData = extraData;
    }

    public List getEntities() {
        return entities;
    }

    public void setEntities(List entities) {
        this.entities = entities;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getExtraData() {
        return extraData;
    }

    public void setExtraData(long extraData) {
        this.extraData = extraData;
    }
}
