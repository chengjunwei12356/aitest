# P1 Features Implementation Roadmap

> **Status**: Deferred for future implementation
> **Decision Date**: 2026-05-11
> **Priority**: Medium-term planning (3-6 months)

---

## Overview

P1 features have been deferred to allow focus on critical P0 security fixes. This document outlines the planned features for future implementation phases.

---

## P1 Feature List

### 1. Customer Management Module (Issue #1)

**Scope:**
- Complete customer CRUD operations (Create, Read, Update, Delete)
- Customer search and filtering
- Customer profile page with loan history
- Frontend UI pages

**Estimated Effort:** 2-3 weeks

**Files to Create/Modify:**
- `CustomerController.java` - REST endpoints
- `CustomerService.java` - Business logic
- `customers.html` - Frontend pages
- `customer-form.html` - Add/Edit forms

---

### 2. Dashboard Statistics with Charts (Issue #2)

**Scope:**
- Loan statistics dashboard
- ECharts integration for data visualization
- Key metrics: total loans, approval rate, pending applications
- Charts: loan trend, status distribution, top customers

**Estimated Effort:** 1-2 weeks

**Dependencies:**
- ECharts library integration
- New dashboard API endpoints

---

### 3. Contract Management Module (Issue #3)

**Scope:**
- Contract template management
- Auto-generate contracts from approved loans
- Online contract signing (digital signature)
- Contract storage and retrieval

**Estimated Effort:** 3-4 weeks

**Complexity:** High (requires digital signature integration)

---

### 4. Post-Loan Management (Issue #4)

**Scope:**
- Repayment schedule generation
- Payment tracking
- Overdue handling and penalties
- Loan extension requests

**Estimated Effort:** 4-5 weeks

**Complexity:** High (complex business logic)

---

### 5. Frontend Technology Stack Upgrade (Issue #21)

**Scope:**
- Migrate from vanilla JS to Vue3 + Element Plus
- Component-based architecture
- State management (Pinia)
- Modern build tooling (Vite)

**Estimated Effort:** 6-8 weeks

**Impact:** Major refactor - affects entire frontend

---

## Implementation Priority Order

Recommended sequence for P1 implementation:

1. **Phase 1** (Month 1-2):
   - Customer Management Module
   - Dashboard Statistics

2. **Phase 2** (Month 3-4):
   - Contract Management
   - Post-Loan Management (basic)

3. **Phase 3** (Month 5-6):
   - Frontend Technology Upgrade
   - Post-Loan Management (advanced)

---

## Prerequisites

Before starting P1 implementation:

- [ ] P0 security fixes deployed and tested
- [ ] Database backup strategy in place
- [ ] Development environment ready
- [ ] Team capacity allocated
- [ ] Stakeholder approval obtained

---

## Success Metrics

| Feature | KPI | Target |
|---------|-----|--------|
| Customer Management | Customer records managed | 100% coverage |
| Dashboard | Load time | < 2 seconds |
| Contract Management | Auto-generation success rate | > 95% |
| Post-Loan | Overdue detection accuracy | 100% |
| Frontend Upgrade | Page load improvement | 30% faster |

---

## Notes

- This roadmap is subject to change based on business priorities
- Each feature should include unit tests and integration tests
- Code review required before merging to main branch
- Documentation must be updated for each new feature

---

*Document created: 2026-05-11*
*Last updated: 2026-05-11*
