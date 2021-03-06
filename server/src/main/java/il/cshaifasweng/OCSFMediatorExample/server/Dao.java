package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class Dao<T> {


    private static Session currentSession;

    private static Transaction currentTransaction;

    private final Class<T> type;

    public Dao(Class<T> type) {
        //Must call constructor like this:
        // Dao<Entity_Name> = new Dao(Entity_Name.class);
        this.type = type;
    }

    public Session openCurrentSession() {
        currentSession = getSessionFactory().openSession();
        return currentSession;
    }

    public Session openCurrentSessionWithTransaction() {
        try {
            currentSession = getSessionFactory().openSession();
            currentTransaction = currentSession.beginTransaction();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally{
            return currentSession;
        }
    }



    public void closeCurrentSession() {
        currentSession.close();
    }

    public void closeCurrentSessionWithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    private static SessionFactory getSessionFactory() throws HibernateException {
        //When creating new annotated class, we must add it here:
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Branch.class);
        configuration.addAnnotatedClass(Menu.class);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(Worker.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(CustomerDetails.class);
        configuration.addAnnotatedClass(Mapp.class);
        configuration.addAnnotatedClass(Tablee.class);
        configuration.addAnnotatedClass(Booking.class);
        configuration.addAnnotatedClass(MealUpdate.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(ImageInfo.class);
        configuration.addAnnotatedClass(PurpleLetter.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public void save(T entity) {
        openCurrentSessionWithTransaction();
        getCurrentSession().save(entity);
        getCurrentSession().flush();
        closeCurrentSessionWithTransaction();

    }


    public void update(T entity) {
        openCurrentSessionWithTransaction();
        getCurrentSession().clear();
        getCurrentSession().update(entity);
        closeCurrentSessionWithTransaction();
    }

    public T findById(int id) {
        openCurrentSessionWithTransaction();
        T book = (T) getCurrentSession().get(type, id);
        closeCurrentSessionWithTransaction();
        return book;
    }

    public T findById2(int id, Session session) {
        T book = (T) session.get(type, id);
        return book;
    }

    public void delete(int id) {
        openCurrentSessionWithTransaction();
        T entity = findById2(id,getCurrentSession());
        getCurrentSession().delete(entity);
        closeCurrentSessionWithTransaction();
    }

    public List<T> findAll() {
        try{
            openCurrentSessionWithTransaction();
            CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
            CriteriaQuery<T> query = builder.createQuery(type);
            Root<T> tRoot = query.from(type);
            query.select(tRoot);
            List<T> books = (List<T>) getCurrentSession().createQuery(query).list();
            closeCurrentSessionWithTransaction();
            return books;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteAll() {
        List<T> entityList = findAll();
        openCurrentSessionWithTransaction();
        for (T entity : entityList) {
            getCurrentSession().delete(entity);
        }
        closeCurrentSessionWithTransaction();
    }
}
