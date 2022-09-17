package Model;

import Entity.Filter;
import Entity.Listing;
import Injector.FindConditionInjector.FindAllInjector;
import Injector.FindConditionInjector.FindGroupNameInListing;
import Injector.FindConditionInjector.FindTitleInjector;
import Injector.IInjector;
import Mapper.ListingMapper;
import UnitofWork.IUnitofWork;
import UnitofWork.Repository;
import Util.JWTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListingModel {
    private IUnitofWork<Listing> repo;
    public ListingModel() {
        repo = new Repository<Listing>(new ListingMapper());
    }

    public ListingModel(IUnitofWork<Listing> repo) {
        this.repo = repo;
    }

    public boolean createListing(Listing listing, String jwt) {
        // Check to see if the jwt token is valid
        try{
            if(!JWTUtil.validateToken(jwt)) {
                // if not valid, return false
                return false;
            }
        } catch (Exception e) {
            // Something went wrong
            return false;
        }

        repo.registerNew(listing);
        try{
            repo.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<Listing> search(List<Filter> filterConditions, String jwt) {
        // Check to see if the jwt token is valid
        try{
            if(!JWTUtil.validateToken(jwt)) {
                // if not valid, return false
                return new ArrayList<>();
            }
        } catch (Exception e) {
            // Something went wrong
            return new ArrayList<>();
        }

        List<Listing> result = new ArrayList<>();

        if(filterConditions == null || filterConditions.isEmpty() ) {
            result = repo.readMulti(new FindAllInjector("listing"), new ArrayList<>());
            return result;
        }

        // Populate the custom findInjector
        for(Filter filter : filterConditions) {
            IInjector inj = getInjector(filter.getFilterKey());

            List<Object> param = new ArrayList<>();
            param.add(filter.getFilterVal());

            List<Listing>temp = repo.readMulti(inj, param);

            if(result.size() == 0) {
                result = temp;
            } else {
                // This is an AND and we want to find the intersection
                Set<Listing> resultSet = result.stream()
                        .distinct()
                        .filter(temp::contains)
                        .collect(Collectors.toSet());
                result = new ArrayList<>(resultSet);
            }
        }
        return result;
    }

    private IInjector getInjector(String key) {
        switch(key) {
            case "title":
                return new FindTitleInjector();
            case "groupName":
                return new FindGroupNameInListing();
        }
        return null;
    }
}
