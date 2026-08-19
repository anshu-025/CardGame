package in.cricket.cricketCardGame.controller;

import in.cricket.cricketCardGame.entity.Cricketer;
import in.cricket.cricketCardGame.service.CricketerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cricketers")
public class CricketerController {

    private final CricketerService cricketerService ;
    public CricketerController(CricketerService cricketerService){
        this.cricketerService = cricketerService ;
    }

    @GetMapping("/get/{id}")
    public Cricketer getCricketer(@PathVariable Integer id){
        Cricketer result =  cricketerService.getCricketer(id);
        return result ;
    }

    @GetMapping("/getAll")
    public List<Cricketer> getAllCricketers(){
        List<Cricketer> result = cricketerService.getAllCricketers() ;
        return result ;
    }

    @PutMapping("/update")
    public Cricketer updateCricketer(@RequestBody Cricketer cricketer){
        Cricketer cricketer1 = cricketerService.updateCricketer(cricketer) ;
        return cricketer1 ;
    }

    @PostMapping("/add")
    public Cricketer saveCricketer(@RequestBody Cricketer cricketer){
        Cricketer cricketer1 = cricketerService.saveCricketer(cricketer) ;
        return cricketer1 ;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCricketer(@PathVariable Integer id){
       cricketerService.deleteCricketer(id);
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllCricketer(){
        cricketerService.deleteAllCricketers();
    }

    @PostMapping("/addAll")
    public List<Cricketer> saveAllCricketers(@RequestBody List<Cricketer> cricketers) {
        return cricketerService.saveAllCricketers(cricketers);
    }

}
