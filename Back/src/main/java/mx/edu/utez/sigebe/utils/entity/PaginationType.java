package mx.edu.utez.sigebe.utils.entity;

public class PaginationType {
    private String filter;
    private int page;
    private int limit;
    private String order;
    private String sortBy;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public String toString() {
        return "PaginationType{" +
                "filter='" + filter + '\'' +
                ", page=" + page +
                ", limit=" + limit +
                ", order='" + order + '\'' +
                ", sortBy='" + sortBy + '\'' +
                '}';
    }
}
