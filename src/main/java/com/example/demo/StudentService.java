package com.example.demo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public String createStudent(@RequestBody Student student) {
        Student insertedStudent = studentRepository.insert(student);
        return "Student created " + insertedStudent.getEmail();
    }
}
