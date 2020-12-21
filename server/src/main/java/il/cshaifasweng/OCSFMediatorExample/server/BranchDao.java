package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public class BranchDao implements Dao<Branch> {
    private static Session currentSession;

    private static Transaction currentTransaction;

    public BranchDao() {
    }

    public Session openCurrentSession() {
        currentSession = getSessionFactory().openSession();
        return currentSession;
    }

    public Session openCurrentSessionwithTransaction() {
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

    public void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Branch.class);
        configuration.addAnnotatedClass(Menu.class);
        configuration.addAnnotatedClass(Meal.class);
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

    public void save(Branch entity) {
        getCurrentSession().save(entity);
        getCurrentSession().flush();
    }

    @Override
    public void update(Branch entity) {
        getCurrentSession().update(entity);
    }
    @Override
    public Branch findById(int id) {

        Branch book = (Branch) getCurrentSession().get(Branch.class, id);
        return book;
    }

    @Override
    public void delete(Branch entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Branch> findAll() {
        CriteriaBuilder builder = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Branch> query = builder.createQuery(Branch.class);
        List<Branch> books = (List<Branch>) getCurrentSession().createQuery(query).list();
        return books;
    }
    @Override
    public void deleteAll() {
        List<Branch> entityList = findAll();
        for (Branch entity : entityList) {
            delete(entity);
        }
    }
}
