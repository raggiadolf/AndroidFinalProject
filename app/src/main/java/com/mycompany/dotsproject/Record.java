package com.mycompany.dotsproject;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
    private String m_name;
    private int m_score;
    private Date m_date;

    public Record(String m_name, int m_score, Date m_date) {
        this.m_name = m_name;
        this.m_score = m_score;
        this.m_date = m_date;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String m_name) {
        this.m_name = m_name;
    }

    public int getScore() {
        return m_score;
    }

    public void setScore(int m_score) {
        this.m_score = m_score;
    }

    public Date getDate() {
        return m_date;
    }

    public void setDate(Date m_date) {
        this.m_date = m_date;
    }

    @Override
    public String toString() {
        return m_name + ", " + m_score + ", " + m_date;
    }
}
