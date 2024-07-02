package com.example;

import com.example.dto.IncomingMessageData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<IncomingMessageData, Long> {
}
