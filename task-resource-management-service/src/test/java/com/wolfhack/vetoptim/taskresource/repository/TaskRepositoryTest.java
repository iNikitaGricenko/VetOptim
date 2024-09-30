package com.wolfhack.vetoptim.taskresource.repository;

import com.wolfhack.vetoptim.taskresource.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testFindAllByPetId() {
        Task task1 = new Task();
        task1.setPetId(123L);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setPetId(123L);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findAllByPetId(123L);

        assertThat(tasks).hasSize(2);
    }
}