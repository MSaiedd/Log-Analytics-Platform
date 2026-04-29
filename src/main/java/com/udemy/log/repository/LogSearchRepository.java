package com.udemy.log.repository;

import com.udemy.log.entity.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogSearchRepository extends ElasticsearchRepository<Log, String> {
}