package it.johnj.jeekol.services;

import it.bhuman.jeekol.entities.Course;
import it.bhuman.jeekol.entities.Student;
import it.bhuman.jeekol.entities.Student.Gender;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

@Stateless
@TransactionManagement( TransactionManagementType.CONTAINER )
public class DummyJPA {
	private final Logger logger = LogManager.getLogManager().getLogger(
			DummyJPA.class.getName());
	private final static String createCorsiSql = "CREATE TABLE corsi ("
			+ "id INT, " + "nome VARCHAR(30)," + "anno INT,"
			+ "PRIMARY KEY(id))";

	private final static String createStudentiSql = "CREATE TABLE studenti ("
			+ "id INT, " + "nome VARCHAR(30)," + "sesso VARCHAR(6),"
			+ "PRIMARY KEY(id))";

	private final static String createFrequentantiSql = "CREATE TABLE frequentanti ("
			+ "id_corso INT, "
			+ "id_studente int,"
			+ "PRIMARY KEY(id_corso, id_studente))";

	@PersistenceUnit(unitName = "CourseManagement")
	EntityManagerFactory emf;
	EntityManager em;
	@Resource
	UserTransaction utx;

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
		} catch (SQLException sqle) {
			logger.log(Level.SEVERE, "impossibile inizializzare il db", sqle);
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

		em = emf.createEntityManager();
		insertTestData();
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
			
			//conn.commit();
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
	
	private void insertTestData() {
		logger.info("inserimento dei dati di test");

		Student s0 = new Student(0, "Andrea", Gender.MALE);
		Student s1 = new Student(1, "Mariano", Gender.MALE);
		Student s2 = new Student(2, "Silvio", Gender.MALE);
		Student s3 = new Student(3, "Stefano", Gender.MALE);
		Student s4 = new Student(4, "Francesca", Gender.FEMALE);
		Student s5 = new Student(5, "Manuela", Gender.FEMALE);
		Student s6 = new Student(6, "Rita", Gender.FEMALE);
		Student s7 = new Student(7, "Paolo", Gender.MALE);
		Student s8 = new Student(8, "Nicoletta", Gender.FEMALE);
		Student s9 = new Student(9, "Bernardo", Gender.MALE);

		Set<Student> ss0 = new HashSet<Student>();

		ss0.add(s0);
		ss0.add(s1);
		ss0.add(s6);

		Set<Student> ss1 = new HashSet<Student>();

		ss1.add(s1);
		ss1.add(s2);
		ss1.add(s7);

		Set<Student> ss2 = new HashSet<Student>();

		ss2.add(s4);
		ss2.add(s5);
		ss2.add(s6);

		Set<Student> ss3 = new HashSet<Student>();

		ss3.add(s2);
		ss3.add(s3);
		ss3.add(s4);
		ss3.add(s5);

		Set<Student> ss4 = new HashSet<Student>();

		ss4.add(s0);
		ss4.add(s1);
		ss4.add(s6);
		ss4.add(s8);
		ss4.add(s9);

		Set<Course> courses = new HashSet<Course>();

		Course c0 = new Course(0, "Analisi 1", 2014);
		c0.setAttendees(ss0);

		Course c1 = new Course(1, "Analisi 2", 2014);
		c1.setAttendees(ss1);

		Course c2 = new Course(2, "Fisica 1", 2014);
		c2.setAttendees(ss2);

		Course c3 = new Course(3, "Fisica 2", 2014);
		c3.setAttendees(ss3);

		Course c4 = new Course(4, "Fondameni di Informatica 1", 2014);
		c4.setAttendees(ss4);

		courses.add(c0);
		courses.add(c1);
		courses.add(c2);
		courses.add(c3);
		courses.add(c4);

		em.persist(c0);
		em.persist(c1);
		em.persist(c2);
		em.persist(c3);
		em.persist(c4);

	}

	public Set<Course> findAllCourses() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Course> cq = cb.createQuery(Course.class);
		Root<Course> allcourse = cq.from(Course.class);
		cq.select(allcourse);
		TypedQuery<Course> q = em.createQuery(cq);
		List<Course> ret = q.getResultList();
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
		for (Course course : ret) {
			courses.add(addStats(course));
		}

		return courses;
	}

	private Course addStats(Course theCourse) {
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

		CourseExt tmp = new CourseExt(theCourse.getId(), theCourse.getName(),
				theCourse.getYear());
		for (Student student : theCourse.getAttendees()) {
			if (student.getGender() == Gender.FEMALE) {
				tmp.female++;
			} else {
				tmp.male++;
			}
		}
		return tmp;
	}

	public Course findCourseById(long id) {
		Course course = em.find(Course.class, id);
		return course;
	}

	public Set<Student> findStudensByCourseId(long id) {
		Set<Student> students = null;
		Course course = findCourseById(id);
		if (course != null) {
			students = course.getAttendees();
		}
		return students;
	}
}
