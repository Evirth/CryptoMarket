package pl.csrv.divinecraft.evirth.cryptomarket.helpers;

import pl.csrv.divinecraft.evirth.cryptomarket.models.PlayerAccount;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public final class XmlSerializationHelper {

    public static void XmlSerialize(String fileName, Object object) throws JAXBException {
        File file = new File(String.format("./plugins/CryptoMarket/Players/%s.xml", fileName));
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(object, file);
    }

    public static Object XmlDeserialize(String fileName) throws JAXBException {
        File file = new File(String.format("./plugins/CryptoMarket/Players/%s.xml", fileName));
        JAXBContext jaxbContext = JAXBContext.newInstance(PlayerAccount.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(file);
    }
}
