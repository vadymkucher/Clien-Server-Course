package practice1;

public class ProductTestPR1 {
    private int price;
    private String name;
    public ProductTestPR1(){

    }
    public ProductTestPR1(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductTestPR1{" +
                "price=" + price +
                ", name='" + name + '\'' +
                '}';
    }
}
