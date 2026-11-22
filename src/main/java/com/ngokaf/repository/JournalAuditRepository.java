package com.ngokaf.repository;

import com.ngokaf.entity.JournalAudit;
import org.springframework.data.repository.Repository;

public interface JournalAuditRepository extends Repository<JournalAudit, Long> {

    JournalAudit save(JournalAudit journalAudit);
}
