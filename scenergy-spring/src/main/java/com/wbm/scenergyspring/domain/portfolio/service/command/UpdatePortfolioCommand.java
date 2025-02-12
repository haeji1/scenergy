package com.wbm.scenergyspring.domain.portfolio.service.command;

import com.wbm.scenergyspring.domain.portfolio.entity.Education;
import com.wbm.scenergyspring.domain.portfolio.entity.Experience;
import com.wbm.scenergyspring.domain.portfolio.entity.Honor;
import com.wbm.scenergyspring.domain.portfolio.entity.PortfolioEtc;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdatePortfolioCommand {
    Long portfolioId;
    Long userId;
    String description;
    List<Education> educations;
    List<PortfolioEtc> etcs;
    List<Experience> experiences;
    List<Honor> honors;
}