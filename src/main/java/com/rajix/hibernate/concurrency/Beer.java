package com.rajix.hibernate.concurrency;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: rpatel
 * Date: 3/12/11
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Beer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)

    String id;
    String name;
    String brewery;
}
