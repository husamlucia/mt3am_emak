package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name= "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    private Menu menu;

    @Column(name = "openh")
    private String openHours;

    @Column(name = "closeh")
    private String closeHours;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getCloseHours() {
        return closeHours;
    }

    public void setCloseHours(String closeHours) {
        this.closeHours = closeHours;
    }

    public Branch(String openHours, String closeHours) {
        this.openHours = openHours;
        this.closeHours = closeHours;
        this.menu = new Menu();
    }
}