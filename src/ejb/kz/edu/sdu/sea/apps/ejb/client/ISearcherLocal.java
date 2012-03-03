package kz.edu.sdu.sea.apps.ejb.client;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import kz.edu.sdu.sea.apps.ejb.client.dto.SearchResultDTO;

@Local
public interface ISearcherLocal {
	List<SearchResultDTO> search(String keyword);
	Map<String,String> search(String keyword, int page);
}
