package ai.taskforge.domain;
import jakarta.persistence.*; import java.util.*;
@Entity @Table(name="projects") public class Project { @Id @GeneratedValue private UUID id; @Column(nullable=false) private String name; private String description; protected Project(){} public Project(String n,String d){name=n;description=d;} public UUID getId(){return id;} public String getName(){return name;} }
