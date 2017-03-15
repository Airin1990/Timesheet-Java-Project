package org.weijie.wallethub.mytimesheet.model;

import java.sql.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SummaryRequestBody {

	@XmlElement List<Long> tids;
    @XmlElement Date start;
    @XmlElement Date end;
    
	public SummaryRequestBody() {
	}

	public List<Long> getTids() {
		return tids;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

}
