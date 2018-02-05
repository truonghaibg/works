/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package test;

import java.util.*;

import test.report.annotations.*;

import com.onwbp.adaptation.*;
import com.orchestranetworks.addon.apis.binding.*;
import com.orchestranetworks.addon.apis.userservice.duplicatecampaign.*;
import com.orchestranetworks.addon.apis.userservice.duplicatecampaign.campaigndefinition.*;
import com.orchestranetworks.schema.*;
import com.orchestranetworks.service.*;
import common.*;

/**
 */
public final class TestDuplicationCampaignDefintionWithCodeGeneration extends ApisTestCase
{
	@Reproduce(Issues = "#31168")
	public void testDuplicationCampaignDefintionWithCodeGeneration()
	{
		String campaignDefinitionCode = "CP2000";
		String campaignDefinitionCodeExcepted = "CP2001";
		String label = "Junit for mantis#31168";
		AdaptationTable campaignDefinitionTable = this.apisDataset.getTable(ApisPaths._Apis_TestDriving_TestConfiguration_CampaignDefinition.getPathInSchema());
		//Step 1: Create new campaign definition
		Map<Path, Object> valueNodes = new HashMap<Path, Object>();
		valueNodes.put(
			ApisPaths._Apis_TestDriving_TestConfiguration_CampaignDefinition._Label,
			label);
		Adaptation campaignDefinitionRecord = this.createRecord(campaignDefinitionTable, valueNodes);
		assertNotNull(campaignDefinitionRecord);
		//Step 2: Modify code for campaign definition
		valueNodes.clear();
		valueNodes.put(
			ApisPaths._Apis_TestDriving_TestConfiguration_CampaignDefinition._Code,
			campaignDefinitionCode);
		Adaptation updateRecord = this.updateRecord(
			campaignDefinitionTable,
			campaignDefinitionRecord,
			valueNodes,
			true);
		//Step 3: Run Duplication campaign definition service
		ProgrammaticService ps = ProgrammaticService.createForSession(
			this.session,
			this.apisDataset.getHome());
		CampaignDefinitionDuplicationProcedure procedure;
		ProcedureResult pr;
		String requestSelection = ApisPaths._Apis_TestDriving_TestConfiguration_CampaignDefinition._Oid.format()
			+ "=" + updateRecord.getOccurrencePrimaryKey().format();
		CampaignDuplicationServiceContext serviceContext = new CampaignDuplicationServiceContext();
		serviceContext.setEntitySelection(new TableViewEntitySelectionImpl(
			campaignDefinitionTable,
			requestSelection));
		Adaptation originalCampaign = serviceContext.getSelectedRecord();
		assertNotNull(originalCampaign);
		procedure = new CampaignDefinitionDuplicationProcedure(serviceContext);
		pr = ps.execute(procedure);
		assertFalse(pr.hasFailed());
		assertNull(pr.getException());
		//Step 4: create new campaign definition and check code generation
		valueNodes.clear();
		valueNodes.put(
			ApisPaths._Apis_TestDriving_TestConfiguration_CampaignDefinition._Label,
			label);
		Adaptation createRecord = this.createRecord(campaignDefinitionTable, valueNodes);
		assertNotNull(createRecord);
		assertEquals(
			createRecord.getString(ApisPaths._Apis_TestDriving_TestConfiguration_CampaignDefinition._Code),
			campaignDefinitionCodeExcepted);
	}
}
