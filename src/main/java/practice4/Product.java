package practice4;

import java.util.LinkedList;
import java.util.List;

public class Product {

    private List<String> groups;
    private String name;
    private int amount;
    private double price;

    public Product() {
    }

    public Product(String name, LinkedList<String> group, int amount, double price) {
        this.groups = group;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
    StringBuilder groupString = new StringBuilder();
        for (String group : groups) {
            groupString.append(group).append(", ");
        }
        return "ProductTestPR1: " + "groups = " + groupString +
                "name = '" + name + '\'' +
                ", amount = " + amount +
                ", price = " + price;
    }
}
