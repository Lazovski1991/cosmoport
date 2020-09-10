package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public List<Ship> readAll(Specification<Ship> shipSpecification) {
        return shipRepository.findAll(shipSpecification);
    }

    @Override
    public Page<Ship> readAll(Specification<Ship> shipSpecification, Pageable pageable) {
        return shipRepository.findAll(shipSpecification, pageable);
    }

    @Override
    public ResponseEntity<Ship> create(Ship ship) {

        if (ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null
                || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null
                || !checkDemands(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }

        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }

        ship.setRating(calculationRating(ship.getSpeed(), ship.getProdDate(), ship.getUsed()));

        shipRepository.saveAndFlush(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ship> read(Long id) {
        if (checkId(id) != null) {
            return checkId(id);
        }

        Ship ship = shipRepository.findById(id).get();
        return new ResponseEntity<Ship>(ship, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<Ship> delete(Long id) {
        if (checkId(id) != null) {
            return checkId(id);
        }
        shipRepository.deleteById(id);
        return new ResponseEntity<Ship>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ship> update(Long id, Ship ship) {
        if (checkId(id) != null) {
            return checkId(id);
        }
        if (!checkDemands(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship shipRead = shipRepository.findById(id).get();

        if (ship.getName() != null) {
            shipRead.setName(ship.getName());
        }
        if (ship.getPlanet() != null) {
            shipRead.setPlanet(ship.getPlanet());
        }
        if (ship.getShipType() != null) {
            shipRead.setShipType(ship.getShipType());
        }
        if (ship.getProdDate() != null) {
            shipRead.setProdDate(ship.getProdDate());
        }
        if (ship.getUsed() != null) {
            shipRead.setUsed(ship.getUsed());
        }
        if (ship.getSpeed() != null) {
            shipRead.setSpeed(ship.getSpeed());
        }
        if (ship.getCrewSize() != null) {
            shipRead.setCrewSize(ship.getCrewSize());
        }
        shipRead.setRating(calculationRating(shipRead.getSpeed(), shipRead.getProdDate(), shipRead.getUsed()));

        shipRepository.saveAndFlush(shipRead);

        return new ResponseEntity<>(shipRead, HttpStatus.OK);

    }

    @Override
    public Specification<Ship> filterName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Ship> filterPlanet(String planet) {
        return (root, query, cb) -> planet == null ? null : cb.like(root.get("planet"), "%" + planet + "%");
    }

    @Override
    public Specification<Ship> filterShipType(ShipType shipType) {
        return (root, query, cb) -> shipType == null ? null : cb.equal(root.get("shipType"), shipType);
    }

    @Override
    public Specification<Ship> filterDate(Long after, Long before) {
        return (root, query, cb) -> {
            if (after == null && before == null)
                return null;
            if (after == null) {
                Date before1 = new Date(before);
                return cb.lessThanOrEqualTo(root.get("prodDate"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return cb.greaterThanOrEqualTo(root.get("prodDate"), after1);
            }
            Date before1 = new Date(before);
            Date after1 = new Date(after);
            return cb.between(root.get("prodDate"), after1, before1);
        };
    }

    @Override
    public Specification<Ship> filterUsed(Boolean isUsed) {
        return (root, query, cb) -> {
            if (isUsed == null) {
                return null;
            }
            if (isUsed) {
                return cb.isTrue(root.get("isUsed"));
            }
            return cb.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterSpeed(Double minSpeed, Double maxSpeed) {
        return (root, query, cb) -> {
            if (minSpeed == null && maxSpeed == null)
                return null;
            if (minSpeed == null)
                return cb.lessThanOrEqualTo(root.get("speed"), maxSpeed);
            if (maxSpeed == null)
                return cb.greaterThanOrEqualTo(root.get("speed"), minSpeed);

            return cb.between(root.get("speed"), minSpeed, maxSpeed);
        };
    }

    @Override
    public Specification<Ship> filterCrewSize(Integer minCrew, Integer maxCrew) {
        return (root, query, cb) -> {
            if (minCrew == null && maxCrew == null)
                return null;
            if (minCrew == null)
                return cb.lessThanOrEqualTo(root.get("crewSize"), maxCrew);
            if (maxCrew == null)
                return cb.greaterThanOrEqualTo(root.get("crewSize"), minCrew);

            return cb.between(root.get("crewSize"), minCrew, maxCrew);
        };
    }

    @Override
    public Specification<Ship> filterRating(Double minRating, Double maxRating) {
        return (root, query, cb) -> {
            if (minRating == null && maxRating == null)
                return null;
            if (minRating == null)
                return cb.lessThanOrEqualTo(root.get("rating"), maxRating);
            if (maxRating == null)
                return cb.greaterThanOrEqualTo(root.get("rating"), minRating);

            return cb.between(root.get("rating"), minRating, maxRating);
        };
    }

    @Override
    public Boolean checkDemands(Ship ship) {

        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            return false;

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            return false;

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            return false;

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            return false;

        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
                return false;
        }
        return true;
    }

    @Override
    public Double calculationRating(Double speed, Date prod, Boolean isUsed) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prod);
        int y = calendar.get((Calendar.YEAR));

        double coeff = 1;
        if (isUsed) {
            coeff = 0.5;
        }
        Double rating = (80 * speed * coeff) / ((3019 - y) + 1);

        return Math.round(rating * Math.pow(10, 2)) / Math.pow(10, 2);
    }

    @Override
    public ResponseEntity<Ship> checkId(Long id) {
        if (id == null || !(id instanceof Number) || id % 1 != 0 || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!shipRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return null;
    }
}
