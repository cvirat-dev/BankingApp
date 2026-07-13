package com.demo.benachrichtigung_service.benachrichtigung;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BenachrichtigungRepository extends
        JpaRepository<Benachrichtigung, Long>,
        JpaSpecificationExecutor<Benachrichtigung>
{
}
