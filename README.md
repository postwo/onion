git
https://github.com/gnidoc327/fastcampus-24-db

swagger 경로
http://localhost:8080/swagger-ui/index.html

# @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
ConstraintMode.NO_CONSTRAINT을 사용하면 외래 키(FK) 제약이 비활성화됩니다. 
✔ 즉, 데이터베이스에서 FK를 생성하지 않고 그냥 bigint 타입으로만 저장


# 에러 
java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean
.<init>(java.lang.Object)' at org.springdoc.core.service.GenericResponseService.
lambda$getGenericMapResponse$8(GenericResponseService.java:690) 
~[springdoc-openapi-starter-common-2.1.0.jar:2.1.0]

발생 이유 : 1. java.lang.NoSuchMethodError는 클래스에서 찾을 수 없는 메서드 호출 때문에 발생합니다. 
           ✔ 일반적으로 라이브러리 버전 충돌이 원인일 가능성이 큽니다
           2. Swagger가 동작하지 않는 원인이 @RestControllerAdvice 때문일 가능성!
             ✔ @RestControllerAdvice는 글로벌 예외를 처리하는 역할을 합니다.
            ✔ 하지만 Swagger(Springdoc OpenAPI)가 예외를 정상적으로 처리하지 못할 경우, 
           UI가 로딩되지 않거나 API 문서가 깨질 수 있습니다. 

해결 방법 : 스프링 버전에 맞게 라이브러리 버전을 맞춰주면 된다 최신이면 최신상태로 

다른 해결 방법 :GlobalExceptionHandler를 수정하여 Swagger 연동 문제 해결
            📌 Swagger가 특정 예외를 처리하지 못하는 경우 발생하는 문제를 방지하려면,
            ResponseEntity를 사용해보세요! ✔ 예외 핸들러에서 String을 직접 반환하는 대신 
            ResponseEntity를 사용하여 JSON 형식으로 반환

지금 이 에러가 터진이유는 GlobalExceptionHandler에서 RestControllerAdvice 어노테이션 때문에 발생한거 같다 
그래서 임시적으로 hidden을 붙여줌으로써 해결 

CompletableFuture는 Java에서 비동기 작업을 처리하는 객체예요! ✔ 즉, 시간이 오래 걸리는 작업을 백그라운드에서 실행하고, 작업이 끝나면 결과를 받을 수 있도록 도와주는 기능