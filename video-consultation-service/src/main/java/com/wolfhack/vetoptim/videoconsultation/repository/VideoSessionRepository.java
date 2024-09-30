package com.wolfhack.vetoptim.videoconsultation.repository;

import com.wolfhack.vetoptim.videoconsultation.model.VideoSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoSessionRepository extends MongoRepository<VideoSession, String> {
    List<VideoSession> findByVeterinarianId(Long veterinarianId);
}