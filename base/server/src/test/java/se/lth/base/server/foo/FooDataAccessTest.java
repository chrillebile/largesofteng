package se.lth.base.server.foo;

import org.junit.Test;
import se.lth.base.server.Config;
import se.lth.base.server.database.BaseDataAccessTest;
import se.lth.base.server.database.DataAccessException;
import se.lth.base.server.foo.Foo;
import se.lth.base.server.foo.FooDataAccess;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * @author Rasmus Ros, rasmus.ros@cs.lth.se
 */
public class FooDataAccessTest extends BaseDataAccessTest {

    private FooDataAccess fooDao = new FooDataAccess(Config.instance().getDatabaseDriver());

    @Test
    public void getNoFoo() {
        assertTrue(fooDao.getAllFoo().isEmpty());
    }

    @Test(expected = DataAccessException.class)
    public void addToNoOne() {
        fooDao.addFoo(-10, "meh");
    }

    @Test
    public void addToUser() {
        Foo data = fooDao.addFoo(TEST.getId(), "user1s data");
        assertEquals(TEST.getId(), data.getUserId());
        assertEquals("user1s data", data.getPayload());
    }

    @Test
    public void getAllDataFromDifferentUsers() {
        fooDao.addFoo(TEST.getId(), "d1");
        assertEquals(1, fooDao.getAllFoo().size());
        fooDao.addFoo(ADMIN.getId(), "d2");
        assertEquals(2, fooDao.getAllFoo().size());
        assertEquals(2L, fooDao.getAllFoo().stream().map(Foo::getUserId).distinct().count());
    }

    @Test
    public void deleteFoo() {
        int userId = 2;
        Foo foo = fooDao.addFoo(userId, "data");
        // TODO: Please finish this implementation
        // it should call the deleteFoo method and check its returned value
        assertTrue(fooDao.deleteFoo(userId, foo.getId()));
    }

    @Test
    public void itShouldNotBePossibleToDeleteOthersFoo() {
        int userId = 1;
        Foo foo = fooDao.addFoo(userId, "data");
        // Even though we provide the same foo id, the method should fail to delete here since the user id is different
        int otherUserId = 2;
        assertFalse(fooDao.deleteFoo(foo.getId(), otherUserId));
    }

    @Test
    public void deleteMissingFoo() {
        // No foo has been added yet, so the foo with id = 1 should not exist
        int userId = 1;
        int fooId = 1;
        assertFalse(fooDao.deleteFoo(userId, fooId));
    }

    @Test
    public void updateFooTotal() {
        int userId = 2;
        Foo foo = fooDao.addFoo(userId, "data");
        fooDao.updateTotal(userId, foo.getId(), 4);
        assertEquals(4, fooDao.getUsersFoo(userId).get(0).getTotal());
    }

    @Test
    public void itShouldNotBePossibleToUpdateOthersFoo() {
        int userId = 1;
        int fooId = 1;
        int total = 1;
        assertFalse(fooDao.updateTotal(userId, fooId, total));
    }

    @Test(expected = DataAccessException.class)
    public void failUpdateToZero() {
        int userId = 1;
        Foo foo = fooDao.addFoo(userId, "will go to zero");
        fooDao.updateTotal(userId, foo.getId(), 0);
    }
}
