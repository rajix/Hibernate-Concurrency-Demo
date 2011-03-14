package com.rajix.hibernate.concurrency;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;


import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

/**
 * Hello world!
 */
public class ConcurrencyTest extends TestCase {



    public SessionFactory getSessionFactory(int isolationLevel) {
        Configuration c = new AnnotationConfiguration().configure();
        c.setProperty("hibernate.connection.isolation", isolationLevel+"");
        return c.buildSessionFactory();

    }

    /**
     * Read Committed Isolation
     *
     * @throws Exception
     */
    @Test
    public void testReadCommitted() throws Exception{
        // reset and initialize database
        SessionFactory sessionFactory=getSessionFactory(Connection.TRANSACTION_READ_COMMITTED);

        Session session1 = sessionFactory.openSession();
        System.out.println(session1.connection().getTransactionIsolation()+"");

        Transaction tx1 = session1.beginTransaction();
        Beer schlaflyPaleAle = new Beer();
        schlaflyPaleAle.brewery="schlafly";
        schlaflyPaleAle.name="Pale Ale";
        session1.save(schlaflyPaleAle);

        List beers = session1.createQuery("from Beer").list();
        assertTrue("Should be one beer",beers.size()==1);
        System.out.println("[tx1] number of beers in the database: " + beers.size()) ;


        // The new transaction cannot see the uncommitted data
        Session session2 = sessionFactory.openSession();
        Transaction tx2 = session2.beginTransaction();
        int numBeers = session2.createQuery("from Beer").list().size();
        assertEquals("Should be zero beers",0,numBeers);
        System.out.println("[tx2]number of beers in the database: " + numBeers) ;

        tx1.commit();

        // session2 should now be able to see the commited data
        numBeers = session2.createQuery("from Beer").list().size();
        assertEquals("Should be one beers",numBeers,1);
        System.out.println("[tx2]number of beers in the database: " + numBeers) ;


        tx2.commit();

    }

    @Test
    public void testSerializable() throws Exception{
        // reset and initialize database
        SessionFactory sessionFactory=getSessionFactory(Connection.TRANSACTION_SERIALIZABLE);

        Session session1 = sessionFactory.openSession();
        System.out.println(session1.connection().getTransactionIsolation()+"");

        Transaction tx1 = session1.beginTransaction();
        Beer schlaflyPaleAle = new Beer();
        schlaflyPaleAle.brewery="schlafly";
        schlaflyPaleAle.name="Pale Ale";
        session1.save(schlaflyPaleAle);

        List beers = session1.createQuery("from Beer").list();
        assertTrue("Should be one beer",beers.size()==1);
        System.out.println("[tx1] number of beers in the database: " + beers.size()) ;


        // The new transaction cannot see the uncommitted data
        Session session2 = sessionFactory.openSession();
        Transaction tx2 = session2.beginTransaction();
        int numBeers = session2.createQuery("from Beer").list().size();
        assertEquals("Should be zero beers",0,numBeers);
        System.out.println("[tx2]number of beers in the database: " + numBeers) ;

        tx1.commit();

        // session2 should now be able to see the commited data
        numBeers = session2.createQuery("from Beer").list().size();
        assertEquals("Should be one beers",0,numBeers);
        System.out.println("[tx2]number of beers in the database: " + numBeers) ;


        tx2.commit();

    }

    public static void main(String[] args) throws Exception{
        ConcurrencyTest ct = new ConcurrencyTest();
        ct.testReadCommitted();
    }

}
