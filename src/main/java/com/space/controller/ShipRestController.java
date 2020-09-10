package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;

import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ShipRestController {

    @Autowired
    private ShipService shipService;

    @RequestMapping(value = "/ships", method = RequestMethod.GET)

    public List<Ship> getAllShip(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String planet,
                                 @RequestParam(required = false) ShipType shipType,
                                 @RequestParam(required = false) Long after,
                                 @RequestParam(required = false) Long before,
                                 @RequestParam(required = false) Boolean isUsed,
                                 @RequestParam(required = false) Double minSpeed,
                                 @RequestParam(required = false) Double maxSpeed,
                                 @RequestParam(required = false) Integer minCrewSize,
                                 @RequestParam(required = false) Integer maxCrewSize,
                                 @RequestParam(required = false) Double minRating,
                                 @RequestParam(required = false) Double maxRating,
                                 @RequestParam(required = true, defaultValue = "ID") ShipOrder order,
                                 @RequestParam(required = true, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(required = true, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return shipService.readAll(Specification.where(shipService.filterName(name)).and(shipService.filterPlanet(planet))
                        .and(shipService.filterShipType(shipType)).and(shipService.filterDate(after, before))
                        .and(shipService.filterUsed(isUsed)).and(shipService.filterSpeed(minSpeed, maxSpeed))
                        .and(shipService.filterCrewSize(minCrewSize, maxCrewSize)).and(shipService.filterRating(minRating, maxRating)),
                pageable).getContent();
    }


    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {

        return shipService.create(ship);
    }


    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)

    public Integer getCountShip(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String planet,
                                @RequestParam(required = false) ShipType shipType,
                                @RequestParam(required = false) Long after,
                                @RequestParam(required = false) Long before,
                                @RequestParam(required = false) Boolean isUsed,
                                @RequestParam(required = false) Double minSpeed,
                                @RequestParam(required = false) Double maxSpeed,
                                @RequestParam(required = false) Integer minCrewSize,
                                @RequestParam(required = false) Integer maxCrewSize,
                                @RequestParam(required = false) Double minRating,
                                @RequestParam(required = false) Double maxRating) {

        return shipService.readAll(Specification.where(shipService.filterName(name)).and(shipService.filterPlanet(planet))
                .and(shipService.filterShipType(shipType)).and(shipService.filterDate(after, before))
                .and(shipService.filterUsed(isUsed)).and(shipService.filterSpeed(minSpeed, maxSpeed))
                .and(shipService.filterCrewSize(minCrewSize, maxCrewSize)).and(shipService.filterRating(minRating, maxRating))).size();
    }


    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        return shipService.read(id);
    }


    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody Ship ship) {
        return shipService.update(id, ship);

    }

    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        return shipService.delete(id);
    }
}
