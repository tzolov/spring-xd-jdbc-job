DROP TABLE IF EXISTS fonecta_demo.analytics;

CREATE TABLE fonecta_demo.analytics AS 
	SELECT segmenttiluokka, count(*) as cnt 
		FROM fonecta_demo.segmenttiluokka 
			GROUP BY segmenttiluokka;