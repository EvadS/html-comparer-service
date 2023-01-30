package com.se.sample.dao.models.repository;


import com.se.sample.entity.FileComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComparisonRepository extends JpaRepository<FileComparison, Long> {

}
