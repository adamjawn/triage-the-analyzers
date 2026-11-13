package team.projectpulse.ram.document;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * PMD UseLocaleWithCaseConversions on {@link RequirementDocumentSpecs#hasTitleLike}: the search text is
 * lower-cased in the JVM's default locale and compared with the database's lower(title). Under a Turkish
 * default locale, "I" lower-cases to the dotless "ı", so a search for "TITLE" asks the database for
 * "%tıtle%" and finds no document titled "Title".
 */
class RequirementDocumentSpecsTest {

    private Locale originalLocale;

    @BeforeEach
    void rememberLocale() {
        this.originalLocale = Locale.getDefault();
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    @SuppressWarnings("unchecked")
    void hasTitleLikeLowerCasesTheSearchTextIndependentlyOfTheDefaultLocale() {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        Root<RequirementDocument> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Path<Object> titlePath = mock(Path.class);
        Expression<String> lowerTitle = mock(Expression.class);
        given(root.get("title")).willReturn(titlePath);
        given(criteriaBuilder.lower(any())).willReturn(lowerTitle);

        RequirementDocumentSpecs.hasTitleLike("TITLE").toPredicate(root, query, criteriaBuilder);

        ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
        verify(criteriaBuilder).like(any(Expression.class), pattern.capture());
        assertThat(pattern.getValue()).isEqualTo("%title%");
    }

}
