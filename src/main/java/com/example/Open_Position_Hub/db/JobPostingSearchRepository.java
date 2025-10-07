package com.example.Open_Position_Hub.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JobPostingSearchRepository {

    private static final String TABLE = "job_posting_entity";

    private final JdbcTemplate jdbc;

    public JobPostingSearchRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static String buildWhereForTitles(List<String> titles) {
        return titles.stream()
            .filter(Objects::nonNull)
            .map(t -> "title LIKE ? ESCAPE '\\\\'")
            .collect(Collectors.joining(" OR "));
    }

    private static List<Object> buildLikeParams(List<String> titles) {
        List<Object> params = new ArrayList<>();
        for (String t : titles) {
            String s = t == null ? "" : t;
            s = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            params.add("%" + s + "%");
        }
        return params;
    }

    private static RowMapper<JobPostingEntity> mapper() {
        return (rs, rowNum) -> new JobPostingEntity(
            rs.getString("title"),
            rs.getString("category"),
            rs.getString("experienceLevel"),
            rs.getString("employmentType"),
            rs.getString("location"),
            rs.getString("detailUrl"),
            rs.getLong("companyId")
        );
    }

    public Page<JobPostingEntity> findByTitles(List<String> titles, Pageable pageable) {

        String where = buildWhereForTitles(titles);
        String orderBy = "ORDER BY created_at DESC";
        String select = """
                SELECT title, category, experience_level AS experienceLevel,
                       employment_type AS employmentType, location, detail_url AS detailUrl, company_id AS companyId
                FROM %s
                WHERE %s
                %s
                LIMIT ? OFFSET ?
            """.formatted(TABLE, where, orderBy);

        List<Object> params = buildLikeParams(titles);
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<JobPostingEntity> content = jdbc.query(select, mapper(), params.toArray());

        long total = countByTitles(titles);
        return new PageImpl<>(content, pageable, total);
    }

    public Page<JobPostingEntity> findAll(Pageable pageable) {
        String orderBy = "ORDER BY created_at DESC";
        String select = """
                SELECT title, category, experience_level AS experienceLevel,
                       employment_type AS employmentType, location, detail_url AS detailUrl, company_id AS companyId
                FROM %s
                %s
                LIMIT ? OFFSET ?
            """.formatted(TABLE, orderBy);

        List<JobPostingEntity> content = jdbc.query(
            select,
            mapper(), pageable.getPageSize(), pageable.getOffset());

        long total = countAll();
        return new PageImpl<>(content, pageable, total);
    }

    private long countByTitles(List<String> titles) {
        String where = buildWhereForTitles(titles);
        String countSql = "SELECT COUNT(*) FROM %s WHERE %s".formatted(TABLE, where);
        try {
            Long n = jdbc.queryForObject(countSql, Long.class, buildLikeParams(titles).toArray());
            return n == null ? 0L : n;
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }

    private long countAll() {
        try {
            Long n = jdbc.queryForObject("SELECT COUNT(*) FROM " + TABLE, Long.class);
            return n == null ? 0L : n;
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }
}
