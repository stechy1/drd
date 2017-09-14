package cz.stechy.drd.model.spell;

import cz.stechy.drd.model.IClonable;
import cz.stechy.drd.model.db.base.OnlineItem;
import cz.stechy.drd.model.spell.parser.SpellParser;
import cz.stechy.drd.model.spell.price.ISpellPrice;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Třída reprezentující kouzlo, které může vyvolat kouzelník, nebo hraničár
 */
public final class Spell extends OnlineItem {

    // region Constants

    // endregion

    // region Variables

    // Název kouzla
    private final StringProperty name = new SimpleStringProperty(this, "name");
    // Název kouzla, jak je uváděno kouzelnických knihách
    private final StringProperty magicName = new SimpleStringProperty(this, "magicName");
    // Popis účinků, připravy a dalších efektů kouzla
    private final StringProperty description = new SimpleStringProperty(this, "description");
    // Typ kouzla podle povolání
    private final ObjectProperty<SpellProfessionType> type = new SimpleObjectProperty<>(this,
        "type");
    // Cena kouzla
    private final ObjectProperty<ISpellPrice> price = new SimpleObjectProperty<>(this, "price");
    // Dosah kouzla v sázích, přičemž platí:
    // -1 = dotyk - kouzlo působí na toho, koho se kouzelník dotkne (i na sebe)
    //  0 = kouzlo působí pouze na kouzelníka
    // >0 = kouzlo působí na všechny, kdo jsou v dosahu
    private final IntegerProperty radius = new SimpleIntegerProperty(this, "radius");
    // Rozhah kouzla - na kolik cílů lze kouzlo poslat
    private final IntegerProperty range = new SimpleIntegerProperty(this, "range");
    // Typ cíle, na který je možné kouzlo poslat
    private final ObjectProperty<SpellTarget> target = new SimpleObjectProperty<>(this, "target");
    // Doba v kolech, která je zapotřebí k vyvolání kouzla
    private final IntegerProperty castTime = new SimpleIntegerProperty(this, "spellCastTime");
    // Doba trvání kouzla, přičemž platí:
    // -1 = Doba bude záležet na dodané magenergii
    //  0 = Instantní - kouzlo bude působit okamžitě, pak účinky vyprší
    // >0 = Počet kol, po které bude kouzlo působit
    private final IntegerProperty duration = new SimpleIntegerProperty(this, "duration");
    //TODO implementovat past
    // Past <=> nebezpečnost kouzla
//    private final ObjectProperty<Trap> trap = new SimpleObjectProperty<>(this, "trap");
    // Obrázek reprezentující kouzlo
    private final ObjectProperty<byte[]> image = new SimpleObjectProperty<>(this, "image");

    // endregion

    // region Constructors

    /**
     * Kopy konstruktor
     *
     * @param spell {@link Spell}
     */
    public Spell(Spell spell) {
        this(spell.getId(), spell.getAuthor(), spell.getName(), spell.getMagicName(),
            spell.getDescription(), spell.getType(),
            new SpellParser(spell.price.get().pack()).parse(), spell.getRadius(), spell.getRange(),
            spell.getTarget(), spell.getCastTime(), spell.getDuration(), spell.getImage(),
            spell.isDownloaded(), spell.isUploaded());
    }

    /**
     * Konstruktor kouzla
     *
     * @param id Id kouzla
     * @param author Autor kouzla
     * @param name Název kouzla
     * @param magicName Magický název kouzla
     * @param description Popis kouzla
     * @param type Typ kouzla podle profese {@link SpellProfessionType}
     * @param price Cena kouzla
     * @param radius Rozsah kouzla v sázích - jak daleko může kouzlo působit
     * @param range Počet cílů
     * @param target Typ cíle {@link SpellTarget}
     * @param castTime Doba vyvolání kouzla (v kolech)
     * @param duration Doba působení kouzla
     * @param image Obrázek reprezentující kouzlo
     * @param downloaded Přiznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     * @param uploaded Příznak určující, zda-li je položka nahrána v online databázi, či nikoliv
     */
    private Spell(String id, String author, String name, String magicName, String description,
        SpellProfessionType type, ISpellPrice price, int radius, int range, SpellTarget target,
        int castTime, int duration, byte[] image, boolean downloaded, boolean uploaded) {
        super(id, author, downloaded, uploaded);

        setName(name);
        setMagicName(magicName);
        setDescription(description);
        setType(type);
        setPrice(price);
        setRadius(radius);
        setRange(range);
        setTarget(target);
        setCastTime(castTime);
        setDuration(duration);
        setImage(image);
    }

    // endregion

    // region Public methods

    @Override
    public <T extends IClonable> T duplicate() {
        return (T) new Spell(this);
    }

    // endregion

    // region Getters & Setters

    public final String getName() {
        return name.get();
    }

    public final ReadOnlyStringProperty nameProperty() {
        return name;
    }

    private void setName(String name) {
        this.name.set(name);
    }

    public final String getMagicName() {
        return magicName.get();
    }

    public final ReadOnlyStringProperty magicNameProperty() {
        return magicName;
    }

    private void setMagicName(String magicName) {
        this.magicName.set(magicName);
    }

    public final String getDescription() {
        return description.get();
    }

    public final ReadOnlyStringProperty descriptionProperty() {
        return description;
    }

    private void setDescription(String description) {
        this.description.set(description);
    }

    public final SpellProfessionType getType() {
        return type.get();
    }

    public final ReadOnlyObjectProperty<SpellProfessionType> typeProperty() {
        return type;
    }

    private void setType(SpellProfessionType type) {
        this.type.set(type);
    }

    public final ISpellPrice getPrice() {
        return price.get();
    }

    public final ReadOnlyObjectProperty<ISpellPrice> priceProperty() {
        return price;
    }

    private void setPrice(ISpellPrice price) {
        this.price.set(price);
    }

    public final int getRadius() {
        return radius.get();
    }

    public final ReadOnlyIntegerProperty radiusProperty() {
        return radius;
    }

    private void setRadius(int radius) {
        this.radius.set(radius);
    }

    public final int getRange() {
        return range.get();
    }

    public final ReadOnlyIntegerProperty rangeProperty() {
        return range;
    }

    private void setRange(int range) {
        this.range.set(range);
    }

    public final SpellTarget getTarget() {
        return target.get();
    }

    public final ReadOnlyObjectProperty<SpellTarget> targetProperty() {
        return target;
    }

    private void setTarget(SpellTarget target) {
        this.target.set(target);
    }

    public final int getCastTime() {
        return castTime.get();
    }

    public final ReadOnlyIntegerProperty castTimeProperty() {
        return castTime;
    }

    private void setCastTime(int castTime) {
        this.castTime.set(castTime);
    }

    public final int getDuration() {
        return duration.get();
    }

    public final ReadOnlyIntegerProperty durationProperty() {
        return duration;
    }

    private void setDuration(int duration) {
        this.duration.set(duration);
    }

    // TODO gettery a settery pro past kouzla

    public final byte[] getImage() {
        return image.get();
    }

    public final ReadOnlyObjectProperty<byte[]> imageProperty() {
        return image;
    }

    private void setImage(byte[] image) {
        this.image.set(image);
    }

    // endregion

    public final class Builder {
        private String id;
        private String author;
        private String name;
        private String magicName;
        private String description;
        private SpellProfessionType type;
        private ISpellPrice price;
        private int radius;
        private int range;
        private SpellTarget target;
        private int castTime;
        private int duration;
        private byte[] image;
        private boolean downloaded;
        private boolean uploaded;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder magicName(String magicName) {
            this.magicName = magicName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(SpellProfessionType type) {
            this.type = type;
            return this;
        }

        public Builder price(ISpellPrice price) {
            this.price = price;
            return this;
        }

        public Builder radius(int radius) {
            this.radius = radius;
            return this;
        }

        public Builder range(int range) {
            this.range = range;
            return this;
        }

        public Builder target(SpellTarget target) {
            this.target = target;
            return this;
        }

        public Builder castTime(int castTime) {
            this.castTime = castTime;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder image(byte[] image) {
            this.image = image;
            return this;
        }

        public Builder downloaded(boolean downloaded) {
            this.downloaded = downloaded;
            return this;
        }

        public Builder uploaded(boolean uploaded) {
            this.uploaded = uploaded;
            return this;
        }

        public Spell build() {
            return new Spell(id, author, name, magicName, description, type, price, radius, range,
                target, castTime, duration, image, downloaded, uploaded);
        }
    }
}
