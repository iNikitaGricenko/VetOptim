package com.wolfhack.vetoptim.taskresource.repository;

import com.wolfhack.vetoptim.taskresource.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
	List<Staff> findByRoleAndAvailableTrue(String role);

    @Query("SELECT s FROM Staff s WHERE s.available = true ORDER BY (SELECT COUNT(t) FROM Task t WHERE t.assignedStaff = s) ASC")
    List<Staff> findAvailableStaffSortedByWorkload();
}