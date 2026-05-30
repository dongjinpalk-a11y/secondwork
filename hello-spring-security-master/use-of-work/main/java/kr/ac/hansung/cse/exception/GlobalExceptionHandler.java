package kr.ac.hansung.cse.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * =====================================================================
 * GlobalExceptionHandler - 전역 예외 처리 핸들러
 * =====================================================================
 *
 * @ControllerAdvice:
 *   - 모든 @Controller에 적용되는 공통 예외 처리기입니다.
 *   - AOP(관점 지향 프로그래밍) 기반으로 동작합니다.
 *   - 각 컨트롤러마다 try-catch를 반복 작성하지 않아도 됩니다.
 *   - @ExceptionHandler, @InitBinder, @ModelAttribute를 전역 적용합니다.
 *
 * [예외 처리 우선순위]
 *   1. 컨트롤러 내부의 @ExceptionHandler가 먼저 적용됩니다.
 *   2. 해당 컨트롤러에 핸들러가 없으면 @ControllerAdvice로 위임합니다.
 *   3. 더 구체적인 예외 타입의 핸들러가 우선 적용됩니다.
 *      예: ProductNotFoundException > RuntimeException > Exception
 *
 * [처리 흐름]
 * Controller에서 예외 발생
 *   → DispatcherServlet이 캐치
 *   → HandlerExceptionResolver가 @ControllerAdvice를 찾아 위임
 *   → 해당 @ExceptionHandler 메서드 실행
 *   → error.html 뷰 렌더링
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ─────────────────────────────────────────────────────────────────
     * 404 Not Found: 상품을 찾을 수 없는 경우
     * ─────────────────────────────────────────────────────────────────
     *
     * @ExceptionHandler(ProductNotFoundException.class):
     *   ProductNotFoundException이 발생하면 이 메서드가 호출됩니다.
     *
     * @ResponseStatus(HttpStatus.NOT_FOUND):
     *   뷰를 반환하더라도 HTTP 응답 상태 코드를 404로 설정합니다.
     *   (기본값은 200 OK)
     */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProductNotFound(ProductNotFoundException ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "상품을 찾을 수 없습니다");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorDetail",
                "요청하신 상품 ID (" + ex.getProductId() + ")에 해당하는 상품이 존재하지 않습니다.");
        return "error"; // → /WEB-INF/views/error.html
    }

    /**
     * ─────────────────────────────────────────────────────────────────
     * 400 Bad Request: 잘못된 입력값
     * ─────────────────────────────────────────────────────────────────
     *
     * Service 레이어의 비즈니스 검증 실패 시 발생합니다.
     * 예) 가격이 음수인 경우 (서비스 레이어 방어 검증)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorTitle", "잘못된 요청");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorDetail", "입력하신 데이터를 확인하고 다시 시도해 주세요.");
        return "error";
    }

    /**
     * ─────────────────────────────────────────────────────────────────
     * 500 Internal Server Error: 데이터베이스 오류
     * ─────────────────────────────────────────────────────────────────
     *
     * DataAccessException:
     *   Spring이 JDBC/JPA 예외를 일관된 계층 구조로 변환한 예외 루트 클래스입니다.
     *   @Repository 빈에서 발생하는 모든 데이터 접근 오류가 이 계층으로 변환됩니다.
     *   예) DataIntegrityViolationException (NOT NULL 위반, 중복 키 등)
     *       QueryTimeoutException (쿼리 타임아웃)
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccessException(DataAccessException ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "데이터베이스 오류");
        model.addAttribute("errorMessage", "데이터베이스 처리 중 오류가 발생했습니다.");
        model.addAttribute("errorDetail", "잠시 후 다시 시도해 주세요. 문제가 지속되면 관리자에게 문의하세요.");
        return "error";
    }

    /**
     * ─────────────────────────────────────────────────────────────────
     * 500 Internal Server Error: 예상치 못한 모든 예외 (최종 안전망)
     * ─────────────────────────────────────────────────────────────────
     *
     * Catch-All Handler: 위의 핸들러가 처리하지 못한 모든 예외를 받습니다.
     * 예외 메시지를 사용자에게 직접 노출하지 않고 일반 메시지를 표시합니다.
     * (보안: 스택 트레이스, DB 구조 등 민감 정보 노출 방지)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "서버 오류");
        model.addAttribute("errorMessage", "예상치 못한 오류가 발생했습니다.");
        model.addAttribute("errorDetail", "잠시 후 다시 시도해 주세요. 문제가 지속되면 관리자에게 문의하세요.");
        return "error";
    }
}
