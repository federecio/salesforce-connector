package org.mule.components;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.components.model.MuleSObject;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class SalesForceTestCase extends FunctionalTestCase
{
    protected SalesForce init()
    {

        SalesForce sf = new SalesForce();
        try
        {
            sf.initialise();
        } catch (InitialisationException e)
        {
            throw new RuntimeException(e);
        }

        sf.setUsername(System.getProperty("login"));
        sf.setPassword(System.getProperty("password"));
        sf.setSecurityToken(System.getProperty("securityKey"));

        return sf;
    }

    public void testConfig() throws Exception
    {
        SalesForce sfdc = muleContext.getRegistry().get("salesforce");
        assertNotNull(sfdc.getUsername());
    }

    public void testContactCreate() throws Exception
    {
        SalesForce sf = init();

        List<MuleSObject> list = new ArrayList<MuleSObject>();
        MuleSObject sObject = new MuleSObject();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("FirstName", "Mule");
        map.put("LastName", "Developer");
        map.put("Department", "Engineering");
        map.put("Title", "Slacker Of An Engineer");
        map.put("Phone", "333-333-3333");
        sObject.setFields(map);
        list.add(sObject);

        List<SaveResult> sr = sf.create("Contact", list);

        assertNotNull(sr);
        assertTrue(sr.size() > 0);
        assertTrue(sr.get(0).isSuccess());
        assertNotNull(sr.get(0).getId());

        // Now delete the item.
        List<String> ids = new ArrayList<String>();
        ids.add(sr.get(0).getId());
        List<DeleteResult> dr = sf.delete(ids);

        assertNotNull(dr);
        assertTrue(dr.size() > 0);
        assertTrue(dr.get(0).isSuccess());
    }

    public void testContactCreateAndDeleteConfig() throws Exception
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("FirstName", "Mule");
        map.put("LastName", "Developer");
        map.put("Department", "Engineering");
        map.put("Title", "Slacker Of An Engineer");
        map.put("Phone", "333-333-3333");

        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://createContact", map, null);

        List<SaveResult> sr = (List<SaveResult>) result.getPayload();

        assertNotNull(sr);
        assertTrue(sr.size() > 0);
        assertTrue(sr.get(0).isSuccess());
        assertNotNull(sr.get(0).getId());

        // Now delete the item.
        List<String> ids = new ArrayList<String>();
        ids.add(sr.get(0).getId());
    }

    public void testContactCreateUpdateAndDeleteConfig() throws Exception
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("FirstName", "Mule");
        map.put("LastName", "Developer");
        map.put("Department", "Engineering");
        map.put("Title", "Slacker Of An Engineer");
        map.put("Phone", "333-333-3333");

        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://createContact", map, null);

        List<SaveResult> sr = (List<SaveResult>) result.getPayload();

        assertNotNull(sr);
        assertTrue(sr.size() > 0);
        assertTrue(sr.get(0).isSuccess());
        assertNotNull(sr.get(0).getId());

        // Now delete the item.
        List<String> ids = new ArrayList<String>();
        ids.add(sr.get(0).getId());

        map.put("Id", sr.get(0).getId());
        map.put("LastName", "UpdateResult");

        result = client.send("vm://updateContact", map, null);

        sr = (List<SaveResult>) result.getPayload();
        assertNotNull(sr);
        assertTrue(sr.size() > 0);
        assertTrue(sr.get(0).isSuccess());
        assertNotNull(sr.get(0).getId());

        result = client.send("vm://deleteContact", ids, null);

        List<DeleteResult> dr = (List<DeleteResult>) result.getPayload();

        assertNotNull(dr);
        assertTrue(dr.size() > 0);
        assertTrue(dr.get(0).isSuccess());
    }

    public void testQueryAndRetrive() throws Exception
    {
        SalesForce sf = init();

        QueryResult qr = sf.querySObject("SELECT Id, Name FROM Account WHERE Name='GenePoint'", 1);
        assertNotNull(qr);
        assertTrue(qr.getRecords().size() > 0);


        String Id = qr.getRecords().get(0).getId();
        List<String> Ids = new ArrayList<String>();
        Ids.add(Id);


        List<MuleSObject> list = sf.retrieve("Name", "Account", Ids);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    public void testQuery() throws Exception
    {
        SalesForce sf = init();

        List<MuleSObject> maps = sf.query("SELECT Id, Name FROM Account WHERE Name='GenePoint'", 1);
        assertNotNull(maps);
        assertTrue(maps.size() > 0);
    }

    public void testQueryConfig() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://query", null, null);

        assertNotNull(result);
        assertNotNull(result.getPayload());
        List<HashMap<String, String>> maps = (List<HashMap<String, String>>) result.getPayload();
        assertTrue(maps.size() > 0);
    }

    public void testTransformerConfig() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("vm://transformerTest", null, null);

        assertNotNull(result);
    }


    @Override
    protected String getConfigResources()
    {
        return "sfdc-conf.xml";
    }
}