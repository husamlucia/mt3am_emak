package il.cshaifasweng.OCSFMediatorExample.entities;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "branches")
public class Branch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "openh")
    private String openHours;

    @Column(name = "closeh")
    private String closeHours;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_id")
    private Menu menu;


    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<MealUpdate> mealUpdates;


    @OneToMany(mappedBy = "br", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Mapp> map;

    @OneToMany(mappedBy = "branch",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderColumn
    private List<Complaint> complaints;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="instruction_id")
    private PurpleLetter purpleLetter;



    public Branch(String openHours, String closeHours) {
        this.openHours = openHours;
        this.closeHours = closeHours;
        this.menu = new Menu();
        this.map = new ArrayList<>();
        this.mealUpdates = new ArrayList<>();
        this.complaints=new ArrayList<>();
        this.purpleLetter = new PurpleLetter();
        this.orders = new ArrayList<>();
    }

    public PurpleLetter getPurpleLetter() {
        return purpleLetter;
    }

    public void setPurpleLetter(PurpleLetter purpleLetter) {

        this.purpleLetter = purpleLetter;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    public Mapp getMap(String area) {

        if (area.equals("inside")) return map.get(0);
        else return map.get(1);

    }




    public List<MealUpdate> getMealUpdates() {
        return mealUpdates;
    }

    public void setMealUpdates(List<MealUpdate> mealUpdates) {
        this.mealUpdates = mealUpdates;
    }

    public List<Booking> book(String date, String time, String area, int persons) throws ParseException {
        Mapp map = this.getMap(area);
        if (map != null) {

            return map.getPossibleBookings(date, time, persons, this.purpleLetter.getMax(area));
        }
        return null;
    }


    public void setMap(String area, Mapp map2) {
        map.add(map2);
    }

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



    public Branch() {

    }

    public OccupationMap getOccupationMap(String date, String hour, String area){

        if(this.getMap(area) != null){
            return this.getMap(area).getOccupationMap(date, hour);
        }
        return null;

    }

    public void addComplaint(Complaint complaint){
        this.complaints.add(complaint);
    }

    public void deleteComplaint(Complaint complaint){
        this.complaints.remove(complaint);
    }

    public void addMeal(Meal meal){
        this.getMenu().addMeal(meal);
        meal.setMenu(this.getMenu());
    }

    public void removeMeal(Meal meal){
        this.getMenu().removeMeal(meal);
        meal.setMenu(null);
    }


    public void addOrder(Order order){
        this.getOrders().add(order);
    }


    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}