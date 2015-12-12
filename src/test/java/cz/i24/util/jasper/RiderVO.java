package cz.i24.util.jasper;

public class RiderVO {

    public RiderVO(String product) {
        this.product = product;
    }

    public String getProduct() {
        return this.product;
    }


    public void setProduct(String product) {
        this.product = product;
    }

    /** [428] Insurance code / Kod */
    private String product;
}
