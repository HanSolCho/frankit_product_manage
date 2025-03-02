조한솔
-

본 프로젝트는 프랜킷 백엔드 과제 관련 프로젝트입니다.
아래 내용은 그에 대한 추가적인 설명입니다.


서비스 흐름에 따른 시나리오 전개
- 
- 해당 프로그램은 매장 관리자들을 위한 상품 및 상품 옵션을 관리하는 프로그램입니다.
- 1.관리자 회원가입 및 로그인 진행
- 2-1.계정의 권한이 관리자 일 경우 모든 계정에 관하여 권한 변경 및 회원 탈퇴 가능
- 2-2.모든 계정은 자신의 계정이 대한 비밀변호 변경 및 회원 탈퇴 가능
- 3.모든 계정은 상품 등록,조회,변경,삭제 가능
- 3-1.상품 조회의 경우 전체 조회, 입력한 상품명 포함 상품 리스트 조회, 기준 값 이상, 기준 값 이하 조회 가능
- 4.모든 계정은 상품 옵션 등록,조회,변경,삭제 가능
- 5. 상품 옵션 등록
- 5-1. 상품 옵션 등록 시 해당 상품의 기존 옵션이 3개 이하일 시만 추가 등록 가능
- 5-2. 상풉 옵션 등록 시 옵션의 타입이 SELECT 일 경우 선택 할 수 있는 옵션 추가 등록
- 6. 상품 옵션 변경
- 6-1 상품 옵션 변경 시 옵션의 타입이 SELECT -> INPUT 변경 시 하위 SELECT 옵션 값 삭제 진행
- 6-2 상풉 옵션 변경 시 변경될 옵션의 타입이 SELECT 일 경우 SELECT 옵션 값 추가
- 7. 상품 옵션 삭제
- 7-1. 상품 옵션 삭제 시 옵션의 타입이 SELECT일 경우 하위 SELECT 옵션 값 삭제 진행
- 8. 상품 옵션 값 삭제
- 필수 기능을 구현하며 프로그램을 사용 시 필요하다고 생각되는 추가 조건들을 구현했습니다(관리자 회원 변경, 상품 가격에 따른 조회)
- 상품 옵션 및 옵션 값 삭제의 경우 CASCADE 조건을 통해 동작 시킬지 코드를 통해 진행할 지 고민하였으나 실제 적용 DB 또는 구조의 변경을 고려하여 초안은 코드를 통해 진행하도록 구현했습니다.


기능 관련 개발 현황 및 API 문서
-
- API 문서의 경우 작성한 adoc 파일을 web 혹은 ide를 통해 볼 경우 정상적으로 index.adoc 을 통해 전체 api 문서 확인 가능
- html화 된 문서의 경우 index.html 파일이 제대로 생성되지 않아 각각의 기능에 따른 api 문서가 따로 만들어져있음.(Member-API.html,Product-API.html.....)
- API 문서 adoc 파일 경로 :
- API 문서 html 파일 경로 :
- postman api 문서도 함께 전달드립니다.


진행 현황
-
- 필수 요구 사항 기능 구현 완료
- Service단 주요 포인트 관련 exception 처리 구현(추가 보완 필요)
- 테스트 코드의 경우 각각의 메소드 단위의 단위 테스트 형식으로 구현
- 테스트 코드의 통합 테스트의 경우 의존성 관련 설정 수정 필요(미완)
- 테스트 코드 빌드 에러로 인해 빌드 시 test 제외 하도록 설정해둔 상태(test 코드 에러 해결 후 수정)
