package kr.ac.hansung.cse.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * =====================================================================
 * ProductForm - 상품 등록/수정 폼 DTO (Data Transfer Object)
 * =====================================================================
 *
 * [DTO 패턴을 사용하는 이유]
 * 1. 관심사 분리: JPA 엔티티(Product)는 DB 매핑 담당,
 *                DTO(ProductForm)는 웹 레이어 데이터 처리 담당
 * 2. 보안(Mass Assignment 방지): 클라이언트가 폼 전송으로 엔티티의
 *    민감한 필드(예: id, 내부 상태 값)를 직접 수정하는 것을 방지합니다.
 * 3. Bean Validation 분리: 엔티티의 DB 제약과 웹 폼 검증 규칙을
 *    독립적으로 관리할 수 있습니다.
 * 4. 유연성: 폼의 필드 구성이 엔티티와 달라도 유연하게 처리 가능합니다.
 *
 * [Bean Validation (JSR-380) 어노테이션 정리]
 * ┌─────────────────┬──────────────────────────────────────────────────┐
 * │ 어노테이션      │ 설명                                             │
 * ├─────────────────┼──────────────────────────────────────────────────┤
 * │ @NotNull        │ null 불허 (빈 문자열 허용)                       │
 * │ @NotEmpty       │ null, 빈 문자열("") 불허 (공백 문자열 허용)      │
 * │ @NotBlank       │ null, 빈 문자열, 공백 문자열 모두 불허           │
 * │ @Size           │ 문자열 길이, 컬렉션 크기 범위 제한               │
 * │ @Min / @Max     │ 정수형 최솟값/최댓값                             │
 * │ @DecimalMin     │ BigDecimal 등 최솟값 (inclusive 옵션 지원)       │
 * │ @Digits         │ 허용 정수부 자릿수, 소수부 자릿수                │
 * │ @Pattern        │ 정규 표현식 패턴 검증                            │
 * │ @Email          │ 이메일 형식 검증                                 │
 * └─────────────────┴──────────────────────────────────────────────────┘
 *
 * 검증 구현체: Hibernate Validator (pom.xml에 hibernate-validator 의존성)
 */
@Getter
@Setter
@NoArgsConstructor // Spring MVC 폼 바인딩 필수: 기본 생성자가 있어야 합니다.
public class ProductForm {

    // 수정 시 식별자로 사용 (등록 시에는 null)
    private Long id;

    /**
     * 상품명
     *
     * @NotBlank: null, "" (빈 문자열), " " (공백 문자열) 모두 거부합니다.
     *   - @NotNull과의 차이: @NotNull은 null만 체크, @NotBlank는 공백도 체크
     * @Size(max=100): DB 컬럼 VARCHAR(100)에 맞게 최대 길이 제한
     */
    @NotBlank(message = "상품명은 필수 입력 항목입니다.")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    private String name;

    /**
     * 카테고리 (선택 입력)
     */
    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다.")
    private String category;

    /**
     * 가격
     *
     * @NotNull: 가격 필드는 반드시 입력해야 합니다.
     *           (숫자 필드는 @NotBlank 대신 @NotNull을 사용합니다.)
     * @DecimalMin: BigDecimal의 최솟값을 지정합니다.
     *   - value = "0": 0원 이상
     *   - inclusive = true (기본값): 0원 포함 (0원 허용)
     * @Digits: 자릿수를 제한합니다.
     *   - integer = 8: 정수부 최대 8자리 (최대 99,999,999원)
     *   - fraction = 2: 소수부 최대 2자리
     */
    @NotNull(message = "가격은 필수 입력 항목입니다.")
    @DecimalMin(value = "0", inclusive = true, message = "가격은 0원 이상이어야 합니다.")
    @Digits(integer = 8, fraction = 2, message = "가격은 최대 99,999,999원까지, 소수점 2자리 이하로 입력해 주세요.")
    private BigDecimal price;

    /**
     * 상품 설명 (선택 입력)
     */
    @Size(max = 1000, message = "상품 설명은 1,000자 이하여야 합니다.")
    private String description;

    // ─────────────────────────────────────────────────────────────────
    // 변환 메서드 (DTO ↔ Entity)
    // ─────────────────────────────────────────────────────────────────

    /**
     * ProductForm → Product 엔티티 변환 (등록 시 사용)
     * id는 DB가 자동 생성하므로 포함하지 않습니다.
     */
    public Product toEntity() {
        return new Product(this.name, null, this.price, this.description);
    }


    /**
     * Product 엔티티 → ProductForm 변환 (수정 폼 초기화 시 사용)
     * 팩토리 메서드 패턴으로 구현하여 외부에서 직접 생성자를 호출하지 않도록 합니다.
     */
    public static ProductForm from(Product product) {
        ProductForm form = new ProductForm();
        form.id = product.getId();
        form.name = product.getName();
        form.category = product.getCategory() != null ? product.getCategory().getName() : null;
        form.price = product.getPrice();
        form.description = product.getDescription();
        return form;
    }
}
