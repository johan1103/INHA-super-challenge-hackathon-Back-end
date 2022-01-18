package HitecIsFuture.demo.web.self_diagnosis;



import HitecIsFuture.demo.web.member.Member;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public interface PersonalDiagnoseFormRepository {

    // 자가진단서 저장
    void save(SelfDiagnoseForm selfDiagnoseForm);

    // 날짜로 자가진단서 조회
    Optional<SelfDiagnoseForm> findByDate(Date date);

    // 모든 자가진단서 조회
    ArrayList<SelfDiagnoseForm> findAll();

    // 해당 자가진단서 repository의 member 객체 반환
    Member getMember();
}