package com.example.demo;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/students")
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Autowired
    private final StudentRepository studentRepository;

    @GetMapping(value = "/all")
    public List<Student> fetchAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping(value = "/create")
    public String createStudent(@RequestBody Student student) {
        Student insertedStudent = studentRepository.insert(student);
        return "Student created " + insertedStudent.getEmail();
    }

    /*@RequestMapping("/delete")
    public String delete(@RequestParam String email){
        studentRepository.delete(email);
        return "Deleted + " + email;
    }

     */

   @RequestMapping("/deleteAll")
    public String deleteAll(){
        studentRepository.deleteAll();
        return "Deleted all records";
   }

    @DeleteMapping("/api/students{id}")
   public void deleteStudent(@PathVariable String id){
       studentRepository.deleteById(id);
    }

    @PutMapping("api/students{id}")
    public Student updateStudentUsingId(String id, Student student){
        Optional<Student> findStudentQuery = studentRepository.findById(id);
        Student studentValues = findStudentQuery.get();
        studentValues.setId(student.getId());
        studentValues.setFirstName(student.getFirstName());
        studentValues.setLastName(student.getLastName());
        studentValues.setAddress(student.getAddress());
        studentValues.setFavouriteSubjects(student.getFavouriteSubjects());
        studentValues.setEmail(student.getEmail());
        studentValues.setGender(student.getGender());

        return studentRepository.save(studentValues);
    }
}
