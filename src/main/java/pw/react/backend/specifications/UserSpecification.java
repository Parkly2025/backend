package pw.react.backend.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pw.react.backend.models.User;


public class UserSpecification {

    public static Specification<User> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isEmpty()) {
                return criteriaBuilder.conjunction(); // Always true
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName == null || firstName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (lastName == null || lastName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasFullName(String fullName) {
        return (root, query, criteriaBuilder) -> {
            if (fullName == null || fullName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String[] nameParts = fullName.split("\\s+"); // Split by one or more spaces
            if (nameParts.length == 1) {
                // Search in firstName or lastName
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + fullName.toLowerCase() + "%");
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + fullName.toLowerCase() + "%");
                return criteriaBuilder.or(firstNamePredicate, lastNamePredicate);
            } else {
                // Search for firstName and lastName separately
                String firstNamePart = nameParts[0];
                String lastNamePart = nameParts[nameParts.length -1];
                Predicate firstNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstNamePart.toLowerCase() + "%");
                Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastNamePart.toLowerCase() + "%");
                return criteriaBuilder.and(firstNamePredicate, lastNamePredicate);
            }

        };
    }
}
