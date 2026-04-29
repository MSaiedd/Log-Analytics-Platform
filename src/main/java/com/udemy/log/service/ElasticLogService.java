package com.udemy.log.service;

import com.udemy.log.entity.Log;
import com.udemy.log.repository.LogSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.range;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.term;

@Service
public class ElasticLogService {

    private final LogSearchRepository logSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticLogService(LogSearchRepository logSearchRepository,
                             ElasticsearchOperations elasticsearchOperations) {
        this.logSearchRepository = logSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Log save(Log log) {
        return logSearchRepository.save(log);
    }

    public Page<Log> search(String service,
                            String level,
                            String message,
                            String from,
                            String to,
                            int page,
                            int size) {

        var boolQuery = bool();

        if (service != null && !service.isBlank()) {
            boolQuery.filter(term(t -> t.field("service").value(service)));
        }

        if (level != null && !level.isBlank()) {
            boolQuery.filter(term(t -> t.field("level").value(level)));
        }

        if (message != null && !message.isBlank()) {
            boolQuery.must(match(m -> m.field("message").query(message)));
        }

        if (from != null && !from.isBlank()) {
            boolQuery.filter(range(r -> r.date(d -> d
                    .field("timestamp")
                    .gte(from)
            )));
        }

        if (to != null && !to.isBlank()) {
            boolQuery.filter(range(r -> r.date(d -> d
                    .field("timestamp")
                    .lte(to)
            )));
        }
        NativeQuery query = NativeQuery.builder()
                .withQuery(boolQuery.build()._toQuery())
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<Log> hits = elasticsearchOperations.search(query, Log.class);

        List<Log> content = new ArrayList<>();
        for (SearchHit<Log> hit : hits.getSearchHits()) {
            content.add(hit.getContent());
        }

        return new PageImpl<>(content, PageRequest.of(page, size), hits.getTotalHits());
    }

    public Instant parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return Instant.now();
        }

        String normalized = timestamp.replace(" ", "T");
        if (!normalized.endsWith("Z")) {
            normalized = normalized + "Z";
        }

        return Instant.parse(normalized);
    }
}