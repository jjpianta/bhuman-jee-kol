/*
 * Copyright (C) 2014 B Human Srl.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package it.johnj.jeekol.services;

import it.bhuman.jeekol.entities.Course;
import it.bhuman.jeekol.entities.Student;
import it.bhuman.jeekol.entities.Student.Gender;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

/**
 * 
 * @author ivan
 */
@Singleton

@TransactionManagement( TransactionManagementType.CONTAINER )
public class DummyDB {
	private final Logger logger = LogManager.getLogManager().getLogger(
			DummyDB.class.getName());

	private final static String createCorsiSql = "CREATE TABLE corsi ("
			+ "id INT, "
			+ "nome VARCHAR(30),"
			+ "anno INT,"
			+ "PRIMARY KEY(id))";

	private final static String createStudentiSql = "CREATE TABLE studenti ("
			+ "id INT, "
			+ "nome VARCHAR(30),"
			+ "sesso VARCHAR(6),"
			+ "PRIMARY KEY(id))";

	private final static String createFrequentantiSql = "CREATE TABLE frequentanti ("
			+ "id_corso INT, "
			+ "id_studente int,"
			+ "PRIMARY KEY(id_corso, id_studente))";

	@Resource(lookup = "java:jboss/datasources/ExampleDS")
	private DataSource ds;

	@PostConstruct
	void init() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			logger.info("inizializzazione del db");

			logger.fine("creazione della tabella corsi");
			stmt.executeUpdate(createCorsiSql);
			
			logger.fine("creazione della tabella studenti");
			stmt.executeUpdate(createStudentiSql);
			
			logger.fine("creazione della tabella frequentanti");
			stmt.executeUpdate(createFrequentantiSql);
			
			logger.fine("inserimento dei dati di test");
			
			stmt.addBatch("insert into corsi values(0, 'Analisi 1', 2014)");
			stmt.addBatch("insert into corsi values(1, 'Analisi 2', 2014)");
			stmt.addBatch("insert into corsi values(2, 'Fisica 1', 2014)");
			stmt.addBatch("insert into corsi values(3, 'Fisica 2', 2014)");
			stmt.addBatch("insert into corsi values(4, 'Fondameni di Informatica 1', 2014)");
			
			stmt.addBatch("insert into studenti values(0, 'Andrea', '" + Gender.MALE + "')");
			stmt.addBatch("insert into studenti values(1, 'Mariano', '" + Gender.MALE + "')");
			stmt.addBatch("insert into studenti values(2, 'Silvio', '" + Gender.MALE + "')");
			stmt.addBatch("insert into studenti values(3, 'Stefano', '" + Gender.MALE + "')");
			stmt.addBatch("insert into studenti values(4, 'Francesca', '" + Gender.FEMALE + "')");
			stmt.addBatch("insert into studenti values(5, 'Manuela', '" + Gender.FEMALE + "')");
			stmt.addBatch("insert into studenti values(6, 'Rita', '" + Gender.FEMALE + "')");
			stmt.addBatch("insert into studenti values(7, 'Paolo', '" + Gender.MALE + "')");
			stmt.addBatch("insert into studenti values(8, 'Nicoletta', '" + Gender.FEMALE + "')");
			stmt.addBatch("insert into studenti values(9, 'Bernardo', '" + Gender.MALE + "')");
			
			stmt.addBatch("insert into frequentanti values(0, 0)");
			stmt.addBatch("insert into frequentanti values(0, 1)");
			stmt.addBatch("insert into frequentanti values(0, 6)");
			
			stmt.addBatch("insert into frequentanti values(1, 1)");
			stmt.addBatch("insert into frequentanti values(1, 2)");
			stmt.addBatch("insert into frequentanti values(1, 7)");
			
			stmt.addBatch("insert into frequentanti values(2, 4)");
			stmt.addBatch("insert into frequentanti values(2, 5)");
			stmt.addBatch("insert into frequentanti values(2, 6)");
			
			stmt.addBatch("insert into frequentanti values(3, 2)");
			stmt.addBatch("insert into frequentanti values(3, 3)");
			stmt.addBatch("insert into frequentanti values(3, 4)");
			stmt.addBatch("insert into frequentanti values(3, 5)");
			
			stmt.addBatch("insert into frequentanti values(4, 0)");
			stmt.addBatch("insert into frequentanti values(4, 1)");
			stmt.addBatch("insert into frequentanti values(4, 6)");
			stmt.addBatch("insert into frequentanti values(4, 8)");
			stmt.addBatch("insert into frequentanti values(4, 9)");
			
			int[] ret = stmt.executeBatch();
			
			logger.fine("inseriti " + ret.length + " record");
			
			logger.info("inizializzazione del db terminata");
		} catch (SQLException sqle) {
			logger.log(Level.SEVERE,
					"impossibile inizializzare il db", sqle);
		} finally {
			if (stmt != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio delle risorse", sqle);
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio della connessione", sqle);
				}
			}
		}
	}

	@PreDestroy
	void cleanup() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			logger.info("eliminazione dei dati di test dal db");
			logger.fine("eliminazione della tabella corsi");
			stmt.executeUpdate("DROP TABLE corsi");
			
			logger.fine("eliminazione della tabella studenti");
			stmt.executeUpdate("DROP TABLE Studenti");
			
			logger.fine("eliminazione della tabella frequentanti");
			stmt.executeUpdate("DROP TABLE Frequentanti");
			
			logger.info("eliminazione dei dati di test dal db completata");
		} catch (SQLException sqle) {
			logger.log(Level.SEVERE,
					"impossibile eliminare i dati di test", sqle);
		} finally {
			if (stmt != null) {
				try { 
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio delle risorse", sqle);
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio della connessione", sqle);
				}
			}
		}
	}
	
	/**
	 * @return the courses
	 */
	public Set<Course> findAllCourses() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		class CourseExt extends Course {
			int male;
			int female;
			
			public CourseExt(long id, String name, int year) {
				super(id, name, year);
			}
			public int getMale() {
				return male;
			}
			public int getFemale() {
				return female;
			}
		}
		
		Set<Course> courses = new TreeSet<Course>(new Comparator<Course>() {
			@Override
			public int compare(Course c1, Course c2) {
				int ret = 0;
				if (c1 == null) {
					if (c2 == null) {
						ret = 0;
					} else {
						ret = 1;
					}
				} else if (c2 == null) {
					ret = 0;
				} else {
					ret = c1.getName().compareTo(c2.getName());
				}
				return ret;
			}
		});
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from corsi");
			while (rs.next()) {
				courses.add(new CourseExt(rs.getInt("id"), rs.getString("nome"), rs.getInt("anno")));
			}
			rs.close();
			
			for (Course course : courses) {
				rs = stmt.executeQuery("select count(*) cnt, s.sesso from frequentanti f "
					+ "join studenti s on s.id = f.id_studente "
					+ "where f.id_corso = " + course.getId()
					+ " group by s.sesso");
				
				while (rs.next()) {
					if (Gender.MALE == Gender.valueOf( rs.getString("sesso"))) {
						((CourseExt)course).male = rs.getInt("cnt");
					} else {
						((CourseExt)course).female = rs.getInt("cnt");
					}
				}
			}
		} catch (SQLException sqle) {
			logger.log(Level.SEVERE, "impossibile caricare la lista dei corsi", sqle);
		} finally {
			if (stmt != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio delle risorse", sqle);
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio della connessione", sqle);
				}
			}
		}
		return courses;
	}

	public Course findCourseById(long id) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Course course = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from corsi where id = " + id);
			while (rs.next()) {
				course= new Course(rs.getInt("id"), rs.getString("nome"), rs.getInt("anno"));
			}
			rs.close();
		} catch (SQLException sqle) {
			logger.log(Level.SEVERE, "impossibile caricare il corso " + id, sqle);
		} finally {
			if (stmt != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio delle risorse", sqle);
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio della connessione", sqle);
				}
			}
		}
		return course;
	}

	public Set<Student> findStudensByCourseId(long id) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Set<Student> students = new TreeSet<Student>(new Comparator<Student>() {
			@Override
			public int compare(Student s1, Student s2) {
				int ret = 0;
				if (s1 == null) {
					if (s2 == null) {
						ret = 0;
					} else {
						ret = 1;
					}
				} else if (s2 == null) {
					ret = 0;
				} else {
					ret = s1.getName().compareTo(s2.getName());
				}
				return ret;
			}
		});
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select s.* from studenti s join frequentanti f on f.id_studente = s.id where f.id_corso = " + id);
			while (rs.next()) {
				students.add(new Student(rs.getInt("id"), rs.getString("nome"), Gender.valueOf(rs.getString("sesso"))));
			}
			rs.close();
		} catch (SQLException sqle) {
			logger.log(Level.SEVERE, "impossibile caricare la lista degli studenti del corso " + id, sqle);
		} finally {
			if (stmt != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio delle risorse", sqle);
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.log(Level.SEVERE,
							"errore nel rilascio della connessione", sqle);
				}
			}
		}
		return students;
	}
}
