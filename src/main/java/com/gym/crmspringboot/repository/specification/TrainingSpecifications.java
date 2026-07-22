package com.gym.crmspringboot.repository.specification;

import com.gym.crmspringboot.model.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class TrainingSpecifications {

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_TRAINING_DATE = "trainingDate";

    private TrainingSpecifications() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<Training> filterTrainings(
            String mainUsername,
            boolean isTrainer,
            LocalDate fromDate,
            LocalDate toDate,
            String secondaryUsername,
            String trainingTypeName
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addMainUserPredicate(predicates, root, cb, mainUsername, isTrainer);
            addDatePredicates(predicates, root, cb, fromDate, toDate);
            addSecondaryUserPredicate(predicates, root, cb, secondaryUsername, isTrainer);
            addTrainingTypePredicate(predicates, root, cb, trainingTypeName);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addMainUserPredicate(
            List<Predicate> predicates,
            Root<Training> root,
            CriteriaBuilder cb,
            String username,
            boolean isTrainer
    ) {
        String joinEntity = isTrainer ? "trainer" : "trainee";
        predicates.add(cb.equal(root.join(joinEntity).get(FIELD_USERNAME), username));
    }

    private static void addDatePredicates(
            List<Predicate> predicates,
            Root<Training> root,
            CriteriaBuilder cb,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(FIELD_TRAINING_DATE), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(FIELD_TRAINING_DATE), toDate));
        }
    }

    private static void addSecondaryUserPredicate(
            List<Predicate> predicates,
            Root<Training> root,
            CriteriaBuilder cb,
            String secondaryUsername,
            boolean isTrainer
    ) {
        if (secondaryUsername != null && !secondaryUsername.isEmpty()) {
            String joinEntity = isTrainer ? "trainee" : "trainer";
            predicates.add(cb.equal(root.join(joinEntity).get(FIELD_USERNAME), secondaryUsername));
        }
    }

    private static void addTrainingTypePredicate(
            List<Predicate> predicates,
            Root<Training> root,
            CriteriaBuilder cb,
            String trainingTypeName
    ) {
        if (trainingTypeName != null && !trainingTypeName.isEmpty()) {
            predicates.add(cb.equal(root.join("trainingType").get("trainingTypeName"), trainingTypeName));
        }
    }
}