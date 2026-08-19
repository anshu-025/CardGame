package in.cricket.cricketCardGame.service;

import in.cricket.cricketCardGame.entity.Cricketer;
import in.cricket.cricketCardGame.repository.CricketerRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;




@Service
public class CricketerService {

    private final CricketerRepository cricketerRepository;

    public CricketerService(CricketerRepository cricketerRepository){
        this.cricketerRepository = cricketerRepository ;
    }

    public Cricketer saveCricketer(Cricketer cricketer) {
        return cricketerRepository.save(cricketer);
    }

    public Cricketer getCricketer(Integer id) {
       Optional<Cricketer> result = cricketerRepository.findById(id);

       if(!result.isPresent()){
           return null ;
       }else{
           return result.get() ;
       }
    }

    public List<Cricketer> getAllCricketers(){
        return cricketerRepository.findAll() ;
    }

    public Cricketer updateCricketer(Cricketer cricketer){
        Optional<Cricketer> result = cricketerRepository.findById(cricketer.getId()) ;

        if(!result.isPresent()) {
            return null;
        }

        result.get().setName(cricketer.getName());
        result.get().setBatting(cricketer.getBatting());
        result.get().setBowling(cricketer.getBowling());
        result.get().setFielding(cricketer.getFielding());
        result.get().setKeeping(cricketer.getKeeping());

        return cricketerRepository.save(result.get());
    }

    public void deleteCricketer(Integer id) {
        cricketerRepository.deleteById(id);
    }

    public void deleteAllCricketers() {
        cricketerRepository.deleteAll();
    }

    public List<Cricketer> saveAllCricketers(List<Cricketer> cricketers) {
        return cricketerRepository.saveAll(cricketers);
    }

}
