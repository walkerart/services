/**
 * 
 */
package org.collectionspace.services;
import org.collectionspace.services.common.vocabulary.AuthorityItemJAXBSchema;

public interface ExhibitionJAXBSchema extends AuthorityItemJAXBSchema {
    final static String EXHIBITIONS_COMMON = "exhibitions_common";
    final static String DISPLAY_NAME = "displayName";
    final static String DISPLAY_NAME_COMPUTED = "displayNameComputed";
    final static String SHORT_DISPLAY_NAME = "shortDisplayName";
    final static String SHORT_DISPLAY_NAME_COMPUTED = "shortDisplayNameComputed";

    final static String EXHIBITION_ORGANIZERS = "exhibitionOrganizers";
    final static String EXHIBITION_ORGANIZER = "exhibitionOrganizer";
    final static String EXHIBITION_VENUE_GROUP_LIST = "exhibitionVenueGroupList";
    final static String EXHIBITION_VENUE_NAME = "venueName";
    final static String EXHIBITION_VENUE_START_DATE = "venueStartDate";
    final static String EXHIBITION_VENUE_END_DATE = "venueEndDate";
    final static String EXHIBITION_VENUE_ATTENDANCE = "venueAttendance";
    final static String EXHIBITION_LOCATIONS_INTERNAL = "exhibitionLocationsInternal";
    final static String EXHIBITION_LOCATION_INTERNAL = "exhibitionLocationInternal";
    final static String EXHIBITION_CURATORS = "exhibitionCurators";
    final static String EXHIBITION_CURATOR = "exhibitionCurator";
    final static String EXHIBITION_PUBLICATION = "exhibitionPublication";
    final static String EXHIBITION_CATALOGUES = "exhibitionCatalogues";
    final static String EXHIBITION_CATALOGUE = "exhibitionCatalogue";
    final static String EXHIBITION_LINKS = "exhibitionLinks";
    final static String EXHIBITION_LINK = "exhibitionLink";
    final static String EXHIBITION_REMARKS = "exhibitionRemarks";

    final static String EXHIBITION_TERM_GROUP_LIST = "exhibitionTermGroupList";
    final static String EXHIBITION_TERM_DISPLAY_NAME = "termDisplayName";
    final static String EXHIBITION_TERM_FORMATTED_DISPLAY_NAME = "termFormattedDisplayName";
    final static String EXHIBITION_TERM_NAME = "termName";
    final static String EXHIBITION_TERM_TYPE = "termType";
    final static String EXHIBITION_TERM_STATUS = "termStatus";
    final static String EXHIBITION_TERM_QUALIFIER = "termQualifier";
    final static String EXHIBITION_TERM_LANGUAGE = "termLanguage";
    final static String EXHIBITION_TERM_PREFFORLANGUAGE = "termPrefForLang";
    final static String EXHIBITION_TERM_SOURCE = "termSource";
    final static String EXHIBITION_TERM_SOURCE_DETAIL = "termSourceDetail";
    final static String EXHIBITION_TERM_SOURCE_ID = "termSourceID";
    final static String EXHIBITION_TERM_SOURCE_NOTE = "termSourceNote";

    final static String EXHIBITION_SHORT_IDENTIFIER = "shortIdentifier";
    final static String EXHIBITION_REFNAME = "refName";
    final static String EXHIBITION_INAUTHORITY = "inAuthority";
}